package raf.tabiin.qurantajweed.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpClient {

    public interface DownloadListener {
        void onSuccess(File file);
        void onFailure(Exception e);
        void onProgress(int progress);
    }

    public void downloadFile(String fileUrl, String destinationPath, DownloadListener listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                URL url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                }

                int fileLength = connection.getContentLength();

                inputStream = new BufferedInputStream(connection.getInputStream());
                outputStream = new FileOutputStream(destinationPath);

                byte[] data = new byte[1024];
                int total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        listener.onProgress((total * 100) / fileLength);
                    }
                    outputStream.write(data, 0, count);
                }

                listener.onSuccess(new File(destinationPath));

            } catch (Exception e) {
                listener.onFailure(e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
