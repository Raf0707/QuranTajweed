package raf.tabiin.qurantajweed.utils;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONManager {

    private static final String TAG = "JSONManager";
    private static final String FILENAME = "data.json";

    private Context context;

    public JSONManager(Context context) {
        this.context = context;
    }

    public void addDataToJSON(String key, String value) {
        // Load existing JSON data
        Map<String, String> jsonData = loadJSONData();

        // Add new data
        jsonData.put(key, value);

        // Convert map to JSON string
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonData);

        // Save JSON string to file
        saveJSONData(jsonString);
    }

    public void editDataInJSON(String key, String newValue) {
        // Load existing JSON data
        Map<String, String> jsonData = loadJSONData();

        // Check if the key exists
        if (jsonData.containsKey(key)) {
            // Update the value
            jsonData.put(key, newValue);

            // Convert map to JSON string
            Gson gson = new Gson();
            String jsonString = gson.toJson(jsonData);

            // Save JSON string to file
            saveJSONData(jsonString);
        } else {
            Log.e(TAG, "Key not found: " + key);
        }
    }

    public void deleteDataFromJSON(String key) {
        // Load existing JSON data
        Map<String, String> jsonData = loadJSONData();

        // Check if the key exists
        if (jsonData.containsKey(key)) {
            // Remove the entry
            jsonData.remove(key);

            // Convert map to JSON string
            Gson gson = new Gson();
            String jsonString = gson.toJson(jsonData);

            // Save JSON string to file
            saveJSONData(jsonString);
        } else {
            Log.e(TAG, "Key not found: " + key);
        }
    }

    private Map<String, String> loadJSONData() {
        Map<String, String> jsonData = new HashMap<>();

        try {
            // Open file input stream
            FileInputStream fis = context.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            // Convert byte array to string
            String json = new String(buffer, "UTF-8");

            // Convert JSON string to map
            Gson gson = new Gson();
            jsonData = gson.fromJson(json, Map.class);
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
            // If file doesn't exist, return an empty map
            jsonData = new HashMap<>();
        }

        return jsonData;
    }

    private void saveJSONData(String jsonData) {
        try {
            // Open file output stream
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            // Write JSON string to file
            fos.write(jsonData.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing JSON file: " + e.getMessage());
        }
    }
}
