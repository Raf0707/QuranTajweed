package raf.tabiin.qurantajweed.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class MailRuDownloader {

    private static final String TAG = "MailRuDownloader";
    private static final String BASE_URL = "https://cloud.mail.ru/public/";

    private Context context;

    public MailRuDownloader(Context context) {
        this.context = context;
    }

    public void downloadAudioFile(String fileName) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + fileName;

        String destination = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;

        client.get(url, new FileAsyncHttpResponseHandler(new File(destination)) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.d(TAG, "File downloaded: " + file.getAbsolutePath());
                Toast.makeText(context, "File downloaded: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, File file) {
                Log.e(TAG, "Download failed. Status code: " + statusCode);
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            }

        });
    }
}

