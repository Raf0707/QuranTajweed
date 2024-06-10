package raf.tabiin.qurantajweed.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.io.InputStream;

import raf.tabiin.qurantajweed.MainActivity;
import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.databinding.ImageItemBinding;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private int count;
    private BookmarkAdapter bookmarkAdapter;
    private MainActivity.OnPageChangedListener onPageChangedListener;

    public ImageAdapter(Context context, int count, ViewPager2 quranPager) {
        this.context = context;
        this.count = count;
        this.bookmarkAdapter = new BookmarkAdapter(context, quranPager);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = String.format("Quran_Tajweed_Classic/%03d.jpg", position + 1);
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
                int clickedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                // Вызываем метод обратного вызова при изменении позиции
                if (onPageChangedListener != null) {
                    onPageChangedListener.onPageChanged(clickedPosition);
                }
            }
        });

        // Устанавливаем выделение для выбранной позиции
        holder.itemView.setSelected(position == onPageChangedListener.getCurrentPosition());
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public void setOnPageChangedListener(MainActivity.OnPageChangedListener listener) {
        this.onPageChangedListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageItemBinding.bind(itemView);
            imageView = binding.imageView;
        }
    }
}