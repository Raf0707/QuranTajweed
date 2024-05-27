package raf.tabiin.qurantajweed.ui.player.res_downloaders;



import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DownloadFilesTask extends AsyncTask<List<String>, Integer, Void> {
    private Context context;

    private static final String URL_PREFIX = "https://ia801605.us.archive.org/3/items/quran__by--mashary-al3afasy---128-kb----604-part-full-quran-604-page--safahat-/Page";
    private static final int NUM_FILES = 604;

    public DownloadFilesTask(Context context) {
        this.context = context;
    }

    @Override
    public Void doInBackground(List<String>... lists) {
        List<String> urls = lists[0];
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            try {
                downloadFile(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress((int) ((i / (float) urls.size()) * 100));
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Integer... progress) {
        // Обновление прогресса, например, прогресс-бар
        // progress[0] содержит текущий прогресс в процентах
    }

    @Override
    public void onPostExecute(Void result) {
        // Действия после завершения загрузки всех файлов
        // Например, показать сообщение или обновить UI
    }

    public void downloadFile(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        InputStream inputStream = new BufferedInputStream(url.openStream());
        File outputFile = new File(context.getExternalFilesDir(null), urlStr.substring(urlStr.lastIndexOf('/') + 1));
        OutputStream outputStream = new FileOutputStream(outputFile);

        byte[] data = new byte[1024];
        int count;
        while ((count = inputStream.read(data)) != -1) {
            outputStream.write(data, 0, count);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
        urlConnection.disconnect();
    }

    public List<String> generateDownloadUrls() {
        List<String> urls = new ArrayList<>();
        for (int i = 1; i <= NUM_FILES; i++) {
            String url = String.format(Locale.US, "%s%03d.mp3", URL_PREFIX, i);
            urls.add(url);
        }
        return urls;
    }
}

