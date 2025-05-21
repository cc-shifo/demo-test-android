1、APK Sign Block之中的V2 Signature Schema Block的组成可以看mylibrary注释
   V2和V3 Signature Schema Block经过私有签名后的数据是指ID后面的value字段。
2、APK Sign Block FORMAT
        // 因为只有一个ID，所以不存在repeated ID-value pairs情况。
        // FORMAT:
        // uint64:  size (excluding this field) // L1 size = pair1 + pairN + 剩余空间
        //  ID-value pair1:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        //  ID-value pair2:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        //  ID-value pairN:     // 可以是私有自定义ID-value
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: apkSignatureSchemeV2Block value
        // uint64:  size (same as the one above)
        // uint128: magic
        // Sign the digests and wrap the signatures and signer info into an APK Signing Block.
3、ID后的value的实际内容为，见generateApkSigningBlock函数
      // 4个字节长度
      // 4字节待签名数据长度 + 待签名数据序列
      // 4字节签名数据长度 + 签名序列
      // 4字节公钥数据长度 + 公钥数据序列
4、ID后的value里面的签名是使用APP签名文件.jks之中的私钥，按照摘要算法SHA-256/512，对待签名数据进行签名的。
5、待签名数据参考generateSignerBlock函数的注释。
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

6、待签名数据数据之中的证书是指APP签名文件jks之中的公钥。

7、验证签名过程
参见generateSignerBlock函数
验证签名过程：签名算法，证书之中的公钥，已经被签名过的数据
签名过程：
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
签名验证过程：
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

8、ApkSignerV2.java.txt是从google android source取的源码