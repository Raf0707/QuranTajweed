package raf.tabiin.qurantajweed.utils;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import raf.tabiin.qurantajweed.model.PageData;

public class JSONManager {

    private static final String TAG = "JSONManager";
    private static final String FILENAME = "data.json";

    private Context context;

    public JSONManager(Context context) {
        this.context = context;
    }

    public List<PageData> loadJSONData() {
        List<PageData> jsonData = new ArrayList<>();

        try {
            // Check if JSON file exists in internal storage
            File file = new File(context.getFilesDir(), FILENAME);
            if (!file.exists()) {
                // If file doesn't exist, copy from assets
                copyJSONFromAssets();
            }

            // Open file input stream
            FileInputStream fis = context.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            // Convert JSON to list of PageData objects
            Type listType = new TypeToken<ArrayList<PageData>>(){}.getType();
            jsonData = new Gson().fromJson(isr, listType);
            isr.close();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
        }

        return jsonData;
    }

    private void copyJSONFromAssets() {
        try {
            // Open file input stream from assets
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(FILENAME));
            // Open file output stream in internal storage
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            // Copy data
            char[] buffer = new char[1024];
            int length;
            while ((length = isr.read(buffer)) > 0) {
                fos.write(new String(buffer, 0, length).getBytes());
            }
            // Close streams
            fos.flush();
            fos.close();
            isr.close();
        } catch (IOException e) {
            Log.e(TAG, "Error copying JSON file from assets: " + e.getMessage());
        }
    }

    public void saveJSONData(List<PageData> jsonData) {
        try {
            // Open file output stream
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            // Convert list of PageData objects to JSON and write to file
            new Gson().toJson(jsonData, osw);
            osw.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing JSON file: " + e.getMessage());
        }
    }

    public void editBookmarkStatus(int pageIndex, boolean isBookmarked) {
        List<PageData> jsonData = loadJSONData();
        if (pageIndex >= 0 && pageIndex < jsonData.size()) {
            jsonData.get(pageIndex).setBookmarked(isBookmarked);
            saveJSONData(jsonData);
        } else {
            Log.e(TAG, "Invalid page index: " + pageIndex);
        }
    }
}
