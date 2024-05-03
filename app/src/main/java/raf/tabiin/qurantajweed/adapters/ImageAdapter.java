package raf.tabiin.qurantajweed.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.io.InputStream;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.model.Bookmark;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private int count;
    private BookmarkAdapter bookmarkAdapter;

    public ImageAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
        this.bookmarkAdapter = new BookmarkAdapter(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = String.format("Quran/%03d.jpg", position+1);
        try {
            InputStream is = context.getAssets().open(fileName);
            Drawable drawable = Drawable.createFromStream(is, null);
            holder.imageView.setImageDrawable(drawable);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Пример добавления закладки по нажатию на изображение
        holder.imageView.setOnClickListener(v -> {
            Bookmark bookmark = new Bookmark(position);
            bookmarkAdapter.addBookmark(bookmark);
        });

        // Пример удаления закладки по долгому нажатию на изображение
        holder.imageView.setOnLongClickListener(v -> {
            bookmarkAdapter.removeBookmark(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}