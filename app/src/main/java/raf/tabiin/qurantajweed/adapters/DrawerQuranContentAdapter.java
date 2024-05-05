package raf.tabiin.qurantajweed.adapters;

import static java.lang.String.format;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.model.QuranItemContent;


public class DrawerQuranContentAdapter extends RecyclerView.Adapter<DrawerQuranContentAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<QuranItemContent> quranDrawer;

    public DrawerQuranContentAdapter(Context context, List<QuranItemContent> quranDrawer) {
        this.quranDrawer = quranDrawer;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.quran_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        QuranItemContent newTitleSure = quranDrawer.get(position);

        holder.sure.setText(format("%s", newTitleSure.getTitle()));

        holder.sure.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return quranDrawer.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView sure;

        ViewHolder(View view) {
            super(view);
            sure = view.findViewById(R.id.sure);
        }
    }
}