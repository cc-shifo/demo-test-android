package com.demo.myhttp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;

public class FileUtils {
    /**
     * 默认缓存大小 8192
     */
    private static final int DEFAULT_BUFFER_SIZE = 2 << 12;

    private FileUtils() {
        //nothing
    }

    public static void writeFromStream(File out, InputStream in) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(out)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while ((n = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
                outputStream.flush();
            }

            outputStream.flush();
        }

        /*try {
            Response<ResponseBody> response = mCall.execute();
            if (response.isSuccessful()) {
                try (ResponseBody b = response.body()) {
                    assert b != null;
                    try (BufferedSource source = b.source()) {
                        BufferedSink sink = Okio.buffer(Okio.sink(file));
                        byte[] buffer = new byte[8192];
                        int n;
                        while ((n = source.read(buffer)) != -1) {
                            sink.write(buffer, 0, n);
                            sink.flush();
                            sink.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
