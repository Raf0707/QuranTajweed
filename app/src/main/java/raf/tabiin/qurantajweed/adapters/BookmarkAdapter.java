package raf.tabiin.qurantajweed.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.model.Bookmark;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private static final String PREFS_NAME = "BookmarkPrefs";
    private static final String KEY_BOOKMARKS = "bookmarks";

    private Context context;
    private List<Bookmark> bookmarks;
    private SharedPreferences preferences;
    private Gson gson;

    public BookmarkAdapter(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.bookmarks = loadBookmarks();
    }

    public List<Bookmark> loadBookmarks() {
        String json = preferences.getString(KEY_BOOKMARKS, null);
        Type type = new TypeToken<List<Bookmark>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveBookmarks() {
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(bookmarks);
        editor.putString(KEY_BOOKMARKS, json);
        editor.apply();
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void addBookmark(Bookmark bookmark) {
        bookmarks.add(bookmark);
        saveBookmarks();
    }

    public void removeBookmark(int position) {
        bookmarks.removeIf(bookmark -> bookmark.getPosition() == position);
        saveBookmarks();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        // Установите данные закладки в ваш ViewHolder
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Здесь объявите элементы интерфейса для отображения данных закладки
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Инициализируйте элементы интерфейса здесь
        }
    }

    public boolean isBookmarked(int position) {
        // Пройдите по списку закладок
        for (Bookmark bookmark : bookmarks) {
            // Проверьте, совпадает ли позиция закладки с заданной
            if (bookmark.getPosition() == position) {
                // Если закладка найдена на данной позиции, верните true
                return true;
            }
        }
        // Если закладка на данной позиции не найдена, верните false
        return false;
    }

}
