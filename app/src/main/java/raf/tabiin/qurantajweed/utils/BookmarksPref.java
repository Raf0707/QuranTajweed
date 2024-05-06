package raf.tabiin.qurantajweed.utils;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class BookmarksPref {
    private final Context context;
    private final Gson gson;
    private final File jsonFile;
    private final Type setType = new TypeToken<Set<Integer>>() {}.getType();
    private Set<Integer> bookmarks;

    public BookmarksPref(Context context) {
        this.context = context;
        gson = new Gson();
        jsonFile = new File(context.getFilesDir(), "bookmarks.json");
        loadBookmarks();
    }

    // Загрузка закладок из JSON-файла
    private void loadBookmarks() {
        if (!jsonFile.exists()) {
            bookmarks = new HashSet<>();
            return;
        }
        try (FileReader reader = new FileReader(jsonFile)) {
            bookmarks = gson.fromJson(reader, setType);
        } catch (IOException e) {
            bookmarks = new HashSet<>();
            e.printStackTrace();
        }
    }

    // Сохранение закладок в JSON-файл
    private void saveBookmarks() {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(bookmarks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Проверяет, есть ли закладка на данной странице
    public boolean isBookmarked(int page) {
        return bookmarks.contains(page);
    }

    // Добавляет закладку на страницу
    public void addBookmark(int page) {
        bookmarks.add(page);
        saveBookmarks();
    }

    // Удаляет закладку со страницы
    public void removeBookmark(int page) {
        bookmarks.remove(page);
        saveBookmarks();
    }

    // Возвращает все закладки
    public Set<Integer> getBookmarks() {
        return bookmarks;
    }
}