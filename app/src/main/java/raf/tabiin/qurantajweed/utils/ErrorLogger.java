package raf.tabiin.qurantajweed.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ErrorLogger {

    private static final String TAG = "ErrorLogger";

    public static void logErrorToFile(Context context, String errorMessage) {
        try {
            // Создаем файл для журнала ошибок
            File logFile = createLogFile(context);
            if (logFile == null) {
                Log.e(TAG, "Failed to create log file");
                return;
            }

            // Получаем текущее время для метки времени в журнале
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            // Сохраняем сообщение об ошибке с меткой времени в файле
            String logMessage = currentTime + ": " + errorMessage + "\n";
            FileOutputStream outputStream = new FileOutputStream(logFile, true);
            outputStream.write(logMessage.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write error log to file: " + e.getMessage());
        }
    }

    private static File createLogFile(Context context) {
        File directory = new File(context.getExternalFilesDir(null), "ErrorLogs");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create directory for error logs");
                return null;
            }
        }
        String fileName = "error_log.txt";
        return new File(directory, fileName);
    }
}
