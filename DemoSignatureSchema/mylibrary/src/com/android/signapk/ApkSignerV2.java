/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.signapk;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * APK Signature Scheme v2 signer.
 *
 * <p>APK Signature Scheme v2 is a whole-file signature scheme which aims to protect every single
 * bit of the APK, as opposed to the JAR Signature Scheme which protects only the names and
 * uncompressed contents of ZIP entries.
 */
public abstract class ApkSignerV2 {
    /*
     * The two main goals of APK Signature Scheme v2 are:
     * 1. Detect any unauthorized modifications to the APK. This is achieved by making the signature
     *    cover every byte of the APK being signed.
     * 2. Enable much faster signature and integrity verification. This is achieved by requiring
     *    only a minimal amount of APK parsing before the signature is verified, thus completely
     *    bypassing ZIP entry decompression and by making integrity verification parallelizable by
     *    employing a hash tree.
     *
     * The generated signature block is wrapped into an APK Signing Block and inserted into the
     * original APK immediately before the start of ZIP Central Directory. This is to ensure that
     * JAR and ZIP parsers continue to work on the signed APK. The APK Signing Block is designed for
     * extensibility. For example, a future signature scheme could insert its signatures there as
     * well. The contract of the APK Signing Block is that all contents outside of the block must be
     * protected by signatures inside the block.
     */

    public static final int SIGNATURE_RSA_PSS_WITH_SHA256 = 0x0101;
    public static final int SIGNATURE_RSA_PSS_WITH_SHA512 = 0x0102;
    public static final int SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256 = 0x0103;
    public static final int SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512 = 0x0104;
    public static final int SIGNATURE_ECDSA_WITH_SHA256 = 0x0201;
    public static final int SIGNATURE_ECDSA_WITH_SHA512 = 0x0202;
    public static final int SIGNATURE_DSA_WITH_SHA256 = 0x0301;
    public static final int SIGNATURE_DSA_WITH_SHA512 = 0x0302;

    /**
     * {@code .SF} file header section attribute indicating that the APK is signed not just with
     * JAR signature scheme but also with APK Signature Scheme v2 or newer. This attribute
     * facilitates v2 signature stripping detection.
     *
     * <p>The attribute contains a comma-separated set of signature scheme IDs.
     */
    public static final String SF_ATTRIBUTE_ANDROID_APK_SIGNED_NAME = "X-Android-APK-Signed";
    // TODO: Adjust the value when signing scheme finalized.
    public static final String SF_ATTRIBUTE_ANDROID_APK_SIGNED_VALUE = "1234567890";

    private static final int CONTENT_DIGEST_CHUNKED_SHA256 = 0;
    private static final int CONTENT_DIGEST_CHUNKED_SHA512 = 1;

    private static final int CONTENT_DIGESTED_CHUNK_MAX_SIZE_BYTES = 1024 * 1024;

    private static final byte[] APK_SIGNING_BLOCK_MAGIC =
          new byte[] {
              0x41, 0x50, 0x4b, 0x20, 0x53, 0x69, 0x67, 0x20,
              0x42, 0x6c, 0x6f, 0x63, 0x6b, 0x20, 0x34, 0x32,
          };
    private static final int APK_SIGNATURE_SCHEME_V2_BLOCK_ID = 0x7109871a;

    private ApkSignerV2() {}

    /**
     * Signer configuration.
     */
    public static final class SignerConfig {
        /** Private key. */
        public PrivateKey privateKey;

        /**
         * Certificates, with the first certificate containing the public key corresponding to
         * {@link #privateKey}.
         */
        public List<X509Certificate> certificates;

        /**
         * List of signature algorithms with which to sign (see {@code SIGNATURE_...} constants).
         * 签名算法ID
         * {@link #SIGNATURE_RSA_PSS_WITH_SHA256}
         * {@link #SIGNATURE_RSA_PSS_WITH_SHA512}等等
         */
        public List<Integer> signatureAlgorithms;
    }

    /**
     * Signs the provided APK using APK Signature Scheme v2 and returns the signed APK as a list of
     * consecutive chunks.
     *
     * <p>NOTE: To enable APK signature verifier to detect v2 signature stripping, header sections
     * of META-INF/*.SF files of APK being signed must contain the
     * {@code X-Android-APK-Signed: true} attribute.
     *
     * @param inputApk contents of the APK to be signed. The APK starts at the current position
     *        of the buffer and ends at the limit of the buffer.
     * @param signerConfigs signer configurations, one for each signer.
     *
     * @throws ApkParseException if the APK cannot be parsed.
     * @throws InvalidKeyException if a signing key is not suitable for this signature scheme or
     *         cannot be used in general.
     * @throws SignatureException if an error occurs when computing digests of generating
     *         signatures.
     */
    public static ByteBuffer[] sign(
            ByteBuffer inputApk,
            List<SignerConfig> signerConfigs)
                    throws ApkParseException, InvalidKeyException, SignatureException {
        // Slice/create a view in the inputApk to make sure that:
        // 1. inputApk is what's between position and limit of the original inputApk, and
        // 2. changes to position, limit, and byte order are not reflected in the original.
        ByteBuffer originalInputApk = inputApk;
        inputApk = originalInputApk.slice();
        inputApk.order(ByteOrder.LITTLE_ENDIAN);

        // Locate ZIP End of Central Directory (EoCD), Central Directory, and check that Central
        // Directory is immediately followed by the ZIP End of Central Directory.
        int eocdOffset = ZipUtils.findZipEndOfCentralDirectoryRecord(inputApk);
        if (eocdOffset == -1) {
            throw new ApkParseException("Failed to locate ZIP End of Central Directory");
        }
        if (ZipUtils.isZip64EndOfCentralDirectoryLocatorPresent(inputApk, eocdOffset)) {
            throw new ApkParseException("ZIP64 format not supported");
        }
        inputApk.position(eocdOffset);
        long centralDirSizeLong = ZipUtils.getZipEocdCentralDirectorySizeBytes(inputApk);
        if (centralDirSizeLong > Integer.MAX_VALUE) {
            throw new ApkParseException(
                    "ZIP Central Directory size out of range: " + centralDirSizeLong);
        }
        int centralDirSize = (int) centralDirSizeLong;
        long centralDirOffsetLong = ZipUtils.getZipEocdCentralDirectoryOffset(inputApk);
        if (centralDirOffsetLong > Integer.MAX_VALUE) {
            throw new ApkParseException(
                    "ZIP Central Directory offset in file out of range: " + centralDirOffsetLong);
        }
        int centralDirOffset = (int) centralDirOffsetLong;
        int expectedEocdOffset = centralDirOffset + centralDirSize;
        if (expectedEocdOffset < centralDirOffset) {
            throw new ApkParseException(
                    "ZIP Central Directory extent too large. Offset: " + centralDirOffset
                            + ", size: " + centralDirSize);
        }
        if (eocdOffset != expectedEocdOffset) {
            throw new ApkParseException(
                    "ZIP Central Directory not immeiately followed by ZIP End of"
                            + " Central Directory. CD end: " + expectedEocdOffset
                            + ", EoCD start: " + eocdOffset);
        }

        // Create ByteBuffers holding the contents of everything before ZIP Central Directory,
        // ZIP Central Directory, and ZIP End of Central Directory.
        inputApk.clear();
        ByteBuffer beforeCentralDir = getByteBuffer(inputApk, centralDirOffset);
        ByteBuffer centralDir = getByteBuffer(inputApk, eocdOffset - centralDirOffset);
        // Create a copy of End of Central Directory because we'll need modify its contents later.
        byte[] eocdBytes = new byte[inputApk.remaining()];
        inputApk.get(eocdBytes);
        ByteBuffer eocd = ByteBuffer.wrap(eocdBytes);
        eocd.order(inputApk.order());

        // Figure which which digests to use for APK contents.
        Set<Integer> contentDigestAlgorithms = new HashSet<>();
        for (SignerConfig signerConfig : signerConfigs) {
            for (int signatureAlgorithm : signerConfig.signatureAlgorithms) {
                contentDigestAlgorithms.add(
                        getSignatureAlgorithmContentDigestAlgorithm(signatureAlgorithm));
            }
            // 签名算法不重复，出现同样的算法时后边的相同算法不会被添加进。
        }

        // Compute digests of APK contents.
        Map<Integer, byte[]> contentDigests; // digest algorithm ID -> digest
        try {
            contentDigests =
                    computeContentDigests(
                            contentDigestAlgorithms,
                            new ByteBuffer[] {beforeCentralDir, centralDir, eocd});
        } catch (DigestException e) {
            throw new SignatureException("Failed to compute digests of APK", e);
        }

        // 因为只有一个ID，所以不存在repeated ID-value pairs情况。
        // FORMAT:
        // uint64:  size (excluding this field) // size = pair1 + pairN + 剩余空间
        //  ID-value pair1:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        //  ID-value pair2:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        //  ID-value pairN:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        // uint64:  size (same as the one above)
        // uint128: magic
        // Sign the digests and wrap the signatures and signer info into an APK Signing Block.
        ByteBuffer apkSigningBlock = ByteBuffer.wrap(generateApkSigningBlock(signerConfigs, contentDigests));

        // Update Central Directory Offset in End of Central Directory Record. Central Directory
        // follows the APK Signing Block and thus is shifted by the size of the APK Signing Block.
        centralDirOffset += apkSigningBlock.remaining();
        eocd.clear();
        ZipUtils.setZipEocdCentralDirectoryOffset(eocd, centralDirOffset);

        // Follow the Java NIO pattern for ByteBuffer whose contents have been consumed.
        originalInputApk.position(originalInputApk.limit());

        // Reset positions (to 0) and limits (to capacity) in the ByteBuffers below to follow the
        // Java NIO pattern for ByteBuffers which are ready for their contents to be read by caller.
        // Contrary to the name, this does not clear the contents of these ByteBuffer.
        beforeCentralDir.clear();
        centralDir.clear();
        eocd.clear();

        // Insert APK Signing Block immediately before the ZIP Central Directory.
        return new ByteBuffer[] {
            beforeCentralDir,
            apkSigningBlock,
            centralDir,
            eocd,
        };
    }

    private static Map<Integer, byte[]> computeContentDigests(
            Set<Integer> digestAlgorithms,
            ByteBuffer[] contents) throws DigestException {
        // For each digest algorithm the result is computed as follows:
        // 1. Each segment of contents is split into consecutive chunks of 1 MB in size.
        //    The final chunk will be shorter iff the length of segment is not a multiple of 1 MB.
        //    No chunks are produced for empty (zero length) segments.
        // 2. The digest of each chunk is computed over the concatenation of byte 0xa5, the chunk's
        //    length in bytes (uint32 little-endian) and the chunk's contents.
        // 3. The output digest is computed over the concatenation of the byte 0x5a, the number of
        //    chunks (uint32 little-endian) and the concatenation of digests of chunks of all
        //    segments in-order.

        int chunkCount = 0;
        for (ByteBuffer input : contents) {
            chunkCount += getChunkCount(input.remaining(), CONTENT_DIGESTED_CHUNK_MAX_SIZE_BYTES);
        }

        final Map<Integer, byte[]> digestsOfChunks = new HashMap<>(digestAlgorithms.size());
        for (int digestAlgorithm : digestAlgorithms) {
            int digestOutputSizeBytes = getContentDigestAlgorithmOutputSizeBytes(digestAlgorithm);
            byte[] concatenationOfChunkCountAndChunkDigests =
                    new byte[5 + chunkCount * digestOutputSizeBytes];
            concatenationOfChunkCountAndChunkDigests[0] = 0x5a;
            setUnsignedInt32LittleEngian(
                    chunkCount, concatenationOfChunkCountAndChunkDigests, 1);
            digestsOfChunks.put(digestAlgorithm, concatenationOfChunkCountAndChunkDigests);
        }

        int chunkIndex = 0;
        byte[] chunkContentPrefix = new byte[5];
        chunkContentPrefix[0] = (byte) 0xa5;
        // Optimization opportunity: digests of chunks can be computed in parallel.
        for (ByteBuffer input : contents) {
            while (input.hasRemaining()) {
                int chunkSize =
                        Math.min(input.remaining(), CONTENT_DIGESTED_CHUNK_MAX_SIZE_BYTES);
                final ByteBuffer chunk = getByteBuffer(input, chunkSize);
                for (int digestAlgorithm : digestAlgorithms) {
                    String jcaAlgorithmName =
                            getContentDigestAlgorithmJcaDigestAlgorithm(digestAlgorithm);
                    MessageDigest md;
                    try {
                        md = MessageDigest.getInstance(jcaAlgorithmName);
                    } catch (NoSuchAlgorithmException e) {
                        throw new DigestException(
                                jcaAlgorithmName + " MessageDigest not supported", e);
                    }
                    // Reset position to 0 and limit to capacity. Position would've been modified
                    // by the preceding iteration of this loop. NOTE: Contrary to the method name,
                    // this does not modify the contents of the chunk.
                    chunk.clear();
                    setUnsignedInt32LittleEngian(chunk.remaining(), chunkContentPrefix, 1);
                    md.update(chunkContentPrefix);
                    md.update(chunk);
                    byte[] concatenationOfChunkCountAndChunkDigests =
                            digestsOfChunks.get(digestAlgorithm);
                    int expectedDigestSizeBytes =
                            getContentDigestAlgorithmOutputSizeBytes(digestAlgorithm);
                    int actualDigestSizeBytes =
                            md.digest(
                                    concatenationOfChunkCountAndChunkDigests,
                                    5 + chunkIndex * expectedDigestSizeBytes,
                                    expectedDigestSizeBytes);
                    if (actualDigestSizeBytes != expectedDigestSizeBytes) {
                        throw new DigestException(
                                "Unexpected output size of " + md.getAlgorithm()
                                        + " digest: " + actualDigestSizeBytes);
                    }
                    // 第1次摘要
                    // 5 length + digest1 + digest2 + ...
                    // digest1, digest2, ... are computed in-order.
                    // digest(n) = digest(0xa5 + 4 length + chunk data)
                    // 有多少算法（不重复，去前面的）就生成多少中摘要
                    // 最终数据：是个列表，一种算法一个项
                    // 参与摘要数据，分段：固定0xa5 + 4 length的摘要内容长度 + 分段1MB内容，末尾不足1MB时去实际内容
                    // 每一列表项内容：固定0x5a + 摘要总长度,产生摘要内存时已计算出来了 + 若干个摘要
                    // 才小于1MB。
                }
                chunkIndex++;
            }
        }

        Map<Integer, byte[]> result = new HashMap<>(digestAlgorithms.size());
        for (Map.Entry<Integer, byte[]> entry : digestsOfChunks.entrySet()) {
            int digestAlgorithm = entry.getKey();
            byte[] concatenationOfChunkCountAndChunkDigests = entry.getValue();
            String jcaAlgorithmName = getContentDigestAlgorithmJcaDigestAlgorithm(digestAlgorithm);
            MessageDigest md;
            try {
                md = MessageDigest.getInstance(jcaAlgorithmName);
            } catch (NoSuchAlgorithmException e) {
                throw new DigestException(jcaAlgorithmName + " MessageDigest not supported", e);
            }
            result.put(digestAlgorithm, md.digest(concatenationOfChunkCountAndChunkDigests));
            // 第2次摘要
            // digest(n) = digest(0x5a + 4 length of digests + digests of chunk)
            // 有多少算法（不重复，去前面的）就生成多少中摘要
            // 最终数据：是个列表，一种算法一个项
            // 参与摘要数据：固定0x5a + 摘要总长度,产生摘要内存时已计算出来了 + 若干个摘要
            // 每个列表项内容：计算对应算法的摘要
        }
        return result;
    }

    private static final int getChunkCount(int inputSize, int chunkSize) {
        return (inputSize + chunkSize - 1) / chunkSize;
    }

    private static void setUnsignedInt32LittleEngian(int value, byte[] result, int offset) {
        result[offset] = (byte) (value & 0xff);
        result[offset + 1] = (byte) ((value >> 8) & 0xff);
        result[offset + 2] = (byte) ((value >> 16) & 0xff);
        result[offset + 3] = (byte) ((value >> 24) & 0xff);
    }

    /**
     * @return
     * // uint64:  size (excluding this field)
     * // ID-value pair:
     * //     uint64:           size (excluding this field)
     * //     uint32:           ID
     * //     (size - 4) bytes: apkSignatureSchemeV2Block value
     * // uint64:  size (same as the one above)
     * // uint128: magic
     */
    private static byte[] generateApkSigningBlock(
            List<SignerConfig> signerConfigs,
            Map<Integer, byte[]> contentDigests) throws InvalidKeyException, SignatureException {

        // 因为只有一个sequence，所以返回值就=4个字节长度 + sequence1
        // 4个字节长度
        // 4字节待签名数据长度 + 待签名数据序列
        // 4字节签名数据长度 + 签名序列
        // 4字节公钥数据长度 + 公钥数据序列
        byte[] apkSignatureSchemeV2Block = generateApkSignatureSchemeV2Block(signerConfigs, contentDigests);
        // FORMAT:
        // uint64:  size (excluding this field)
        // ID-value pair:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        // uint64:  size (same as the one above)
        // uint128: magic
        return generateApkSigningBlock(apkSignatureSchemeV2Block);
    }

    /**
     * @param apkSignatureSchemeV2Block
     *         // 4个字节长度
     *         // 4字节待签名数据长度 + 待签名数据序列
     *         // 4字节签名数据长度 + 签名序列
     *         // 4字节公钥数据长度 + 公钥数据序列
     * @return 因为只有1个ID，所以只返回1个ID-value pair
     *   // FORMAT:
     *         // uint64:  size (excluding this field)
     *         // ID-value pair:
     *         //     uint64:           size (excluding this field)
     *         //     uint32:           ID
     *         //     (size - 4) bytes: value
     *         // uint64:  size (same as the one above)
     *         // uint128: magic
     */
    private static byte[] generateApkSigningBlock(byte[] apkSignatureSchemeV2Block) {
        // FORMAT:
        // uint64:  size (excluding this field)
        // repeated ID-value pairs:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: value
        // uint64:  size (same as the one above)
        // uint128: magic

        int resultSize =
                8 // size [L1]
                + 8 + 4 + apkSignatureSchemeV2Block.length // v2Block as ID-value pair
                + // other 8 + 4 ID + value.length // v2Block as ID-value pair
                + // tmp
                + 8 // size [L2]
                + 16 // magic
                ;
        ByteBuffer result = ByteBuffer.allocate(resultSize);
        result.order(ByteOrder.LITTLE_ENDIAN);
        long blockSizeFieldValue = resultSize - 8;
        result.putLong(blockSizeFieldValue);

        long pairSizeFieldValue = 4 + apkSignatureSchemeV2Block.length;
        result.putLong(pairSizeFieldValue);
        result.putInt(APK_SIGNATURE_SCHEME_V2_BLOCK_ID);
        result.put(apkSignatureSchemeV2Block);

        result.putLong(blockSizeFieldValue);
        result.put(APK_SIGNING_BLOCK_MAGIC);

        return result.array();
    }

    /**
     * @return 因为只有一个sequence，所以返回值就=4个字节长度 + sequence1
     *   // 4个字节长度
     *   // 4字节待签名数据长度 + 待签名数据序列
     *   // 4字节签名数据长度 + 签名序列
     *   // 4字节公钥数据长度 + 公钥数据序列
     */
    private static byte[] generateApkSignatureSchemeV2Block(
            List<SignerConfig> signerConfigs,
            Map<Integer, byte[]> contentDigests) throws InvalidKeyException, SignatureException {
        // FORMAT:
        // * length-prefixed sequence of length-prefixed signer blocks.

        List<byte[]> signerBlocks = new ArrayList<>(signerConfigs.size());
        int signerNumber = 0;
        for (SignerConfig signerConfig : signerConfigs) {
            signerNumber++;
            byte[] signerBlock;
            try {
                // 4字节待签名数据长度 + 待签名数据序列
                // 4字节签名数据长度 + 签名序列
                // 4字节公钥数据长度 + 公钥数据序列
                signerBlock = generateSignerBlock(signerConfig, contentDigests);
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Signer #" + signerNumber + " failed", e);
            } catch (SignatureException e) {
                throw new SignatureException("Signer #" + signerNumber + " failed", e);
            }
            // signerBlock = 4个字节长度 + 待签名数据 + 4个字节长度 + 签名 + 4个字节长度 + 公钥数据
            signerBlocks.add(signerBlock);
        }

        // 4个字节长度 + sequence1 + 4个字节长度 + sequence2 + 4个字节长度 + ... sequenceN
        // 因为只有一个sequence，所以返回值就=4个字节长度 + sequence1
        // 4个字节长度
        // 4字节待签名数据长度 + 待签名数据序列
        // 4字节签名数据长度 + 签名序列
        // 4字节公钥数据长度 + 公钥数据序列
        return encodeAsSequenceOfLengthPrefixedElements(
                new byte[][] {
                    // sequence = 4个字节长度 + signer1Block + 4个字节长度 + signer2Block + 4个字节长度 + ... signerNBlock
                    encodeAsSequenceOfLengthPrefixedElements(signerBlocks),
                });
    }

    // 返回
    // 4字节待签名数据长度 + 待签名数据序列
    // 4字节签名数据长度 + 签名序列
    // 4字节公钥数据长度 + 公钥数据序列
    private static byte[] generateSignerBlock(
            SignerConfig signerConfig,
            Map<Integer, byte[]> contentDigests) throws InvalidKeyException, SignatureException {
        if (signerConfig.certificates.isEmpty()) {
            throw new SignatureException("No certificates configured for signer");
        }
        PublicKey publicKey = signerConfig.certificates.get(0).getPublicKey();

        byte[] encodedPublicKey = encodePublicKey(publicKey);

        V2SignatureSchemeBlock.SignedData signedData = new V2SignatureSchemeBlock.SignedData();
        try {
            signedData.certificates = encodeCertificates(signerConfig.certificates);
        } catch (CertificateEncodingException e) {
            throw new SignatureException("Failed to encode certificates", e);
        }

        List<Pair<Integer, byte[]>> digests =
                new ArrayList<>(signerConfig.signatureAlgorithms.size());
        for (int signatureAlgorithm : signerConfig.signatureAlgorithms) {
            int contentDigestAlgorithm =
                    getSignatureAlgorithmContentDigestAlgorithm(signatureAlgorithm);
            byte[] contentDigest = contentDigests.get(contentDigestAlgorithm);
            if (contentDigest == null) {
                throw new RuntimeException(
                        getContentDigestAlgorithmJcaDigestAlgorithm(contentDigestAlgorithm)
                        + " content digest for "
                        + getSignatureAlgorithmJcaSignatureAlgorithm(signatureAlgorithm)
                        + " not computed");
            }
            digests.add(Pair.create(signatureAlgorithm, contentDigest));
            // digests: 签名算法ID + 与签名算法对应的摘要算法的摘要
        }
        signedData.digests = digests;

        V2SignatureSchemeBlock.Signer signer = new V2SignatureSchemeBlock.Signer();
        // 待签名数据序列字段
        // FORMAT:
        // * 摘要区域字段(存放摘要序列)
        //   length-prefixed sequence of length-prefixed digests 摘要区域字段:
        //   L-->摘要区域数据长度，占4字节
        //   value--> 摘要区域数据
        //            * 摘要1数据 length-prefixed bytes: digest of contents:
        //              固定4字节，数值4+4=8
        //              uint32: signature algorithm ID，签名算法ID，占4字节
        //              L-->摘要长度，占4字节
        //              value-->摘要内容
        //            * 摘要3数据 length-prefixed bytes: digest of contents:
        //              固定4字节，数值4+4=8
        //              uint32: signature algorithm ID，签名算法ID，占4字节
        //              L-->摘要长度，占4字节
        //              value-->摘要内容
        //              ...
        //            * 摘要n数据 length-prefixed bytes: digest of contents:
        //              固定4字节，数值4+4=8
        //              uint32: signature algorithm ID，签名算法ID，占4字节
        //              L-->摘要长度，占4字节
        //              value-->摘要内容
        // * 证书区域字段(存放证书序列)
        //   length-prefixed sequence of certificates:
        //   L-->证书区域数据长度，占4字节
        //   value-->证书区域数据
        //            * 证书1 length-prefixed bytes: X.509 certificate (ASN.1 DER encoded) of contents
        //              L-->证书长度，占4字节
        //              value-->证书内容
        //            * 证书2 length-prefixed bytes: X.509 certificate (ASN.1 DER encoded) of contents
        //              L-->证书长度，占4字节
        //              value-->证书内容
        //             ...
        //            * 证书n length-prefixed bytes: X.509 certificate (ASN.1 DER encoded) of contents
        //              L-->证书长度，占4字节
        //              value-->证书内容
        // * 额外属性区域字段(存放额外属性序列)
        //   length-prefixed sequence of length-prefixed additional attributes:
        //   L-->额外属性区域数据长度，占4字节
        //   value-->额外属性区域数据
        //            * 属性1
        //              属性1长度length，占4字节
        //              属性1ID，占4字节
        //              属性1内容，内容长度=length - 4
        //            * 属性2
        //              属性2长度length，占4字节
        //              属性2ID，占4字节
        //              属性2内容，内容长度=length - 4
        //             ...
        //            * 属性n
        //              属性n长度length，占4字节
        //              属性nID，占4字节
        //              属性n内容，内容长度=length - 4
        signer.signedData = encodeAsSequenceOfLengthPrefixedElements(new byte[][] {
                // 摘要1, 摘要2, ...摘要n = 4字节(数值固定等于8,4+4)+ 4字节 签名算法ID + 4 摘要长度 + 摘要内容
            encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(signedData.digests),
                // 内容：证书1[4字节长度 + 内容] + 证书2[4字节长度 + 内容] + ...的序列
                // @param sequence 与签名算法对应的签名证书的数据。byte[不同签名规则的index][签名算法对应证书内容]
                // @return 内容：证书1[4字节长度 + 内容] + 证书2[4字节长度 + 内容] + ...的序列
                // 内容总长度=所有证书长度 + 算法种类个数*4
            encodeAsSequenceOfLengthPrefixedElements(signedData.certificates),
            // additional attributes
            new byte[0],
        });
        signer.publicKey = encodedPublicKey;
        signer.signatures = new ArrayList<>();
        for (int signatureAlgorithm : signerConfig.signatureAlgorithms) {
            // 获取签名算法ID对应的签名算法
            Pair<String, ? extends AlgorithmParameterSpec> signatureParams =
                    getSignatureAlgorithmJcaSignatureAlgorithm(signatureAlgorithm);
            String jcaSignatureAlgorithm = signatureParams.getFirst();
            AlgorithmParameterSpec jcaSignatureAlgorithmParams = signatureParams.getSecond();
            byte[] signatureBytes;
            try {
                // 获取指定签名算法的签名对象
                Signature signature = Signature.getInstance(jcaSignatureAlgorithm);
                // 初始化生成签名数据用的私钥
                signature.initSign(signerConfig.privateKey);
                if (jcaSignatureAlgorithmParams != null) {
                    // 初始化标准签名规范对象
                    signature.setParameter(jcaSignatureAlgorithmParams);
                }
                // 设置被签名数据
                signature.update(signer.signedData);
                // 签名
                signatureBytes = signature.sign();
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Failed sign using " + jcaSignatureAlgorithm, e);
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                    | SignatureException e) {
                throw new SignatureException("Failed sign using " + jcaSignatureAlgorithm, e);
            }

            try {
                Signature signature = Signature.getInstance(jcaSignatureAlgorithm);
                // 初始化验证签名对象的公钥
                signature.initVerify(publicKey);
                if (jcaSignatureAlgorithmParams != null) {
                    signature.setParameter(jcaSignatureAlgorithmParams);
                }
                signature.update(signer.signedData);
                if (!signature.verify(signatureBytes)) {
                    throw new SignatureException("Signature did not verify");
                }
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Failed to verify generated " + jcaSignatureAlgorithm
                        + " signature using public key from certificate", e);
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                    | SignatureException e) {
                throw new SignatureException("Failed to verify generated " + jcaSignatureAlgorithm
                        + " signature using public key from certificate", e);
            }

            // 签名算法ID，签名
            signer.signatures.add(Pair.create(signatureAlgorithm, signatureBytes));
        }

        // FORMAT:
        // * length-prefixed signed data 4 byte length + 数据
        // * length-prefixed sequence of length-prefixed signatures:
        //   * uint32: signature algorithm ID
        //   * length-prefixed bytes: signature of signed data
        // * length-prefixed bytes: public key (X.509 SubjectPublicKeyInfo, ASN.1 DER encoded)


        // 4字节待签名数据长度 + 待签名数据序列
        // 4字节签名数据长度 + 签名序列
        // 4字节公钥数据长度 + 公钥数据序列
        return encodeAsSequenceOfLengthPrefixedElements(
                new byte[][] {
                    signer.signedData,
                        // 签名算法ID
                        // SIGNATURE_RSA_PSS_WITH_SHA256 = 0x0101;
                        // SIGNATURE_RSA_PSS_WITH_SHA512 = 0x0102;
                        // SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256 = 0x0103;
                        // SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512 = 0x0104;
                        // SIGNATURE_ECDSA_WITH_SHA256 = 0x0201;
                        // SIGNATURE_ECDSA_WITH_SHA512 = 0x0202;
                        // SIGNATURE_DSA_WITH_SHA256 = 0x0301;
                        // SIGNATURE_DSA_WITH_SHA512 = 0x0302;
                        // 签名序列 FORMAT:
                        // 签名序列(由签名拼接而成，签名是使用私钥对待签名数据进行签名产生的)
                        // 签名1: 4字节（数值等于8）+ 4字节签名算法ID + 4字节签名长度 + 签名内容
                        // 签名2: 4字节（数值等于8）+ 4字节签名算法ID + 4字节签名长度 + 签名内容
                        // ...
                        // 签名n: 4字节（数值等于8）+ 4字节签名算法ID + 4字节签名长度 + 签名内容
                    encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(
                            signer.signatures),
                        // 公钥数据：用于验证签名的
                    signer.publicKey,
                });
    }

    private static final class V2SignatureSchemeBlock {
        private static final class Signer {
            // 待签名数据
            public byte[] signedData;
            // Pair<签名算法ID，签名>. 签名是使用私钥对待签名数据进行签名产生的
            public List<Pair<Integer, byte[]>> signatures;
            // 验证签名的公钥，X.509 SubjectPublicKeyInfo, ASN.1 DER encoded)从证书之中获取
            public byte[] publicKey;
        }

        private static final class SignedData {
            // Pair<签名算法ID，摘要内容>
            public List<Pair<Integer, byte[]>> digests;
            // 签名公钥证书
            public List<byte[]> certificates;
        }
    }

    private static byte[] encodePublicKey(PublicKey publicKey) throws InvalidKeyException {
        byte[] encodedPublicKey = null;
        if ("X.509".equals(publicKey.getFormat())) {
            encodedPublicKey = publicKey.getEncoded();
        }
        if (encodedPublicKey == null) {
            try {
                encodedPublicKey =
                        KeyFactory.getInstance(publicKey.getAlgorithm())
                                .getKeySpec(publicKey, X509EncodedKeySpec.class)
                                .getEncoded();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new InvalidKeyException(
                        "Failed to obtain X.509 encoded form of public key " + publicKey
                                + " of class " + publicKey.getClass().getName(),
                        e);
            }
        }
        if ((encodedPublicKey == null) || (encodedPublicKey.length == 0)) {
            throw new InvalidKeyException(
                    "Failed to obtain X.509 encoded form of public key " + publicKey
                            + " of class " + publicKey.getClass().getName());
        }
        return encodedPublicKey;
    }

    public static List<byte[]> encodeCertificates(List<X509Certificate> certificates)
            throws CertificateEncodingException {
        List<byte[]> result = new ArrayList<>();
        for (X509Certificate certificate : certificates) {
            result.add(certificate.getEncoded());
        }
        return result;
    }

    /**
     *
     * @param sequence X509Certificate证书内容。与签名算法对应的签名证书的数据。
     * @return 内容：证书1[4字节长度 + 内容] + 证书2[4字节长度 + 内容] + ...的序列
     */
    private static byte[] encodeAsSequenceOfLengthPrefixedElements(List<byte[]> sequence) {
        // 内容：证书1[4字节长度 + 内容] + 证书2[4字节长度 + 内容] + ...的序列
        return encodeAsSequenceOfLengthPrefixedElements(
                sequence.toArray(new byte[sequence.size()][]));
    }

    /**
     * @param sequence byte[数据index][数据]
     * @return 内容：数据1[4字节长度 + 内容] + 数据2[4字节长度 + 内容] + ...的序列
     * 例如：
     * @code sequence 与签名算法对应的签名证书的数据。byte[不同签名规则的index][签名算法对应证书内容]
     * @code 内容：证书1[4字节长度 + 内容] + 证书2[4字节长度 + 内容] + ...的序列
     * 内容总长度=所有证书长度 + 算法种类个数*4
     */
    private static byte[] encodeAsSequenceOfLengthPrefixedElements(byte[][] sequence) {
        int payloadSize = 0;
        for (byte[] element : sequence) {
            payloadSize += 4 + element.length;
        }
        ByteBuffer result = ByteBuffer.allocate(payloadSize);
        result.order(ByteOrder.LITTLE_ENDIAN);
        for (byte[] element : sequence) {
            result.putInt(element.length);
            result.put(element);
        }
        return result.array();
      }

    /**
     * @param sequence Pair<ID或KEY，ID或KEY对应的数据>
     * @return  总长度=所有数据的长度+ ID或KEY个数*12
     * 4 总长度+ 4 签名算法ID + 4 摘要长度 + 摘要内容
     * L: 8 + 数据的长度
     *  value: 4 ID + 4 数据的长度 + 数据的内容
     * 例如
     * @code sequence Pair<摘要算法ID，摘要内容>
     * @code 总长度=所有摘要内容的长度+ 算法ID种类个数*12
     * 4 总长度+ 4 签名算法ID + 4 摘要长度 + 摘要内容
     * L: 8 + 摘要的长度
     *  value: 4 签名算法ID + 4 摘要长度 + 摘要内容
     */
    private static byte[] encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(
            List<Pair<Integer, byte[]>> sequence) {
        int resultSize = 0;
        for (Pair<Integer, byte[]> element : sequence) {
            resultSize += 12 + element.getSecond().length;
        }
        ByteBuffer result = ByteBuffer.allocate(resultSize);
        result.order(ByteOrder.LITTLE_ENDIAN);
        for (Pair<Integer, byte[]> element : sequence) {
            byte[] second = element.getSecond();
            result.putInt(8 + second.length);
            result.putInt(element.getFirst());
            result.putInt(second.length);
            result.put(second);
        }
        return result.array();
    }

    /**
     * Relative <em>get</em> method for reading {@code size} number of bytes from the current
     * position of this buffer.
     *
     * <p>This method reads the next {@code size} bytes at this buffer's current position,
     * returning them as a {@code ByteBuffer} with start set to 0, limit and capacity set to
     * {@code size}, byte order set to this buffer's byte order; and then increments the position by
     * {@code size}.
     */
    private static ByteBuffer getByteBuffer(ByteBuffer source, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size: " + size);
        }
        int originalLimit = source.limit();
        int position = source.position();
        int limit = position + size;
        if ((limit < position) || (limit > originalLimit)) {
            throw new BufferUnderflowException();
        }
        source.limit(limit);
        try {
            ByteBuffer result = source.slice();
            result.order(source.order());
            source.position(limit);
            return result;
        } finally {
            source.limit(originalLimit);
        }
    }

    /**
     * 根据签名算法创建标准的签名规范对象
     * @param sigAlgorithm 签名算法
     * @return 标准的签名规范对象
     */
    private static Pair<String, ? extends AlgorithmParameterSpec>
            getSignatureAlgorithmJcaSignatureAlgorithm(int sigAlgorithm) {
        switch (sigAlgorithm) {
            case SIGNATURE_RSA_PSS_WITH_SHA256:
                return Pair.create(
                        "SHA256withRSA/PSS",
                        new PSSParameterSpec(
                                "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 256 / 8, 1));
            case SIGNATURE_RSA_PSS_WITH_SHA512:
                return Pair.create(
                        "SHA512withRSA/PSS",
                        new PSSParameterSpec(
                                "SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 512 / 8, 1));
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256:
                return Pair.create("SHA256withRSA", null);
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512:
                return Pair.create("SHA512withRSA", null);
            case SIGNATURE_ECDSA_WITH_SHA256:
                return Pair.create("SHA256withECDSA", null);
            case SIGNATURE_ECDSA_WITH_SHA512:
                return Pair.create("SHA512withECDSA", null);
            case SIGNATURE_DSA_WITH_SHA256:
                return Pair.create("SHA256withDSA", null);
            case SIGNATURE_DSA_WITH_SHA512:
                return Pair.create("SHA512withDSA", null);
            default:
                throw new IllegalArgumentException(
                        "Unknown signature algorithm: 0x"
                                + Long.toHexString(sigAlgorithm & 0xffffffff));
        }
    }

    private static int getSignatureAlgorithmContentDigestAlgorithm(int sigAlgorithm) {
        switch (sigAlgorithm) {
            case SIGNATURE_RSA_PSS_WITH_SHA256:
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256:
            case SIGNATURE_ECDSA_WITH_SHA256:
            case SIGNATURE_DSA_WITH_SHA256:
                return CONTENT_DIGEST_CHUNKED_SHA256;
            case SIGNATURE_RSA_PSS_WITH_SHA512:
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512:
            case SIGNATURE_ECDSA_WITH_SHA512:
            case SIGNATURE_DSA_WITH_SHA512:
                return CONTENT_DIGEST_CHUNKED_SHA512;
            default:
                throw new IllegalArgumentException(
                        "Unknown signature algorithm: 0x"
                                + Long.toHexString(sigAlgorithm & 0xffffffff));
        }
    }

    private static String getContentDigestAlgorithmJcaDigestAlgorithm(int digestAlgorithm) {
        switch (digestAlgorithm) {
            case CONTENT_DIGEST_CHUNKED_SHA256:
                return "SHA-256";
            case CONTENT_DIGEST_CHUNKED_SHA512:
                return "SHA-512";
            default:
                throw new IllegalArgumentException(
                        "Unknown content digest algorthm: " + digestAlgorithm);
        }
    }

    private static int getContentDigestAlgorithmOutputSizeBytes(int digestAlgorithm) {
        switch (digestAlgorithm) {
            case CONTENT_DIGEST_CHUNKED_SHA256:
                return 256 / 8;
            case CONTENT_DIGEST_CHUNKED_SHA512:
                return 512 / 8;
            default:
                throw new IllegalArgumentException(
                        "Unknown content digest algorthm: " + digestAlgorithm);
        }
    }

    /**
     * Indicates that APK file could not be parsed.
     */
    public static class ApkParseException extends Exception {
        private static final long serialVersionUID = 1L;

        public ApkParseException(String message) {
            super(message);
        }

        public ApkParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
