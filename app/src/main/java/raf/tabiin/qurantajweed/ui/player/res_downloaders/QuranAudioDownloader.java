package raf.tabiin.qurantajweed.ui.player.res_downloaders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import raf.tabiin.qurantajweed.utils.AsyncHttpClient;

public class QuranAudioDownloader {

    private static final String URL_PREFIX = "https://ia801605.us.archive.org/3/items/quran__by--mashary-al3afasy---128-kb----604-part-full-quran-604-page--safahat-/Page";
    private static final int NUM_FILES = 604;

    private ExecutorService executorService;

    public QuranAudioDownloader() {
        // Создаем пул потоков для параллельной загрузки файлов
        executorService = Executors.newFixedThreadPool(10); // Здесь можно указать количество потоков
    }

    public void downloadFiles(String destinationPath) {
        List<String> urls = generateDownloadUrls();
        for (String url : urls) {
            executorService.execute(new DownloadTask(url, destinationPath));
        }
    }

    private List<String> generateDownloadUrls() {
        List<String> urls = new ArrayList<>();
        for (int i = 1; i <= NUM_FILES; i++) {
            String url = String.format(Locale.US, "%s%03d.mp3", URL_PREFIX, i);
            urls.add(url);
        }
        return urls;
    }

    private class DownloadTask implements Runnable {
        private String url;
        private String destinationPath;

        public DownloadTask(String url, String destinationPath) {
            this.url = url;
            this.destinationPath = destinationPath;
        }

        @Override
        public void run() {
            // Создаем экземпляр AsyncHttpClient
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            // Выполняем загрузку файла
            asyncHttpClient.downloadFile(url, destinationPath, new AsyncHttpClient.DownloadListener() {
                @Override
                public void onSuccess(File file) {
                    // Обработка успешной загрузки файла
                    // Здесь можно добавить код для обновления прогресса загрузки или другие действия
                    System.out.println("File downloaded: " + file.getAbsolutePath());
                }

                @Override
                public void onFailure(Exception e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onProgress(int progress) {

                }

            });
        }
    }


    // Добавьте методы для проверки загруженности файлов и другие необходимые методы
}

