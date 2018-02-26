package com.mockserverlibrary.util;

import java.io.IOException;
import java.io.InputStream;

import android.util.Base64;
import android.util.Base64OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * Created by ac on 2018/2/26.
 */

public class FileUtils {

    /**
     * 讀取檔案.
     * @param filePath file path
     * @return InputStream
     */
    public static InputStream readFile(String filePath) {
        InputStream inputStream = null;// You can get an inputStream using any
        // IO API
        try {
            inputStream = MockUtil.readFileInputStream(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    /**
     * 取得檔案base64 data string.
     * @param filePath file path
     * @return String
     */
    public static String getBase64Data(String filePath) {
        InputStream inputStream = null;// You can get an inputStream using any
        // IO API
        try {
            inputStream = MockUtil.readFileInputStream(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[8192];
        int bytesRead;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64OutputStream output64 = new Base64OutputStream(output
                , Base64.NO_PADDING);

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            output64.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String data = output64.toString();

        return data;
    }

}
