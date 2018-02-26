package util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class Base64Util {

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
        Base64OutputStream output64 = new Base64OutputStream(output);

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