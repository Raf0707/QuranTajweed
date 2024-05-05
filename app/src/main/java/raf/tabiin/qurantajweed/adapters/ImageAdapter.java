package raf.tabiin.qurantajweed.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.databinding.ImageItemBinding;
import raf.tabiin.qurantajweed.model.Bookmark;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private int count;
    private BookmarkAdapter bookmarkAdapter;
    private int clickedPosition = -1;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedPosition = position;
                notifyDataSetChanged();
            }
        });

        /*// Отмечаем элемент, если он был выбран
        if (clickedPosition == position) {
            // Меняем цвет или добавляем другие визуальные изменения
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_item_color));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }*/
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public int getPosition() {
        return clickedPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialButton setBookmarkPage;
        ImageItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ImageItemBinding.bind(itemView);
            imageView = binding.imageView;
        }
    }
}