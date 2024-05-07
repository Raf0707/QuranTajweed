package raf.tabiin.qurantajweed.adapters;

import static java.lang.String.format;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.util.List;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.model.QuranItemContent;


public class DrawerQuranContentAdapter extends RecyclerView.Adapter<DrawerQuranContentAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<QuranItemContent> quranDrawer;
    private ViewPager2 quranPager;

    private int[] numPageSures = new int[]{
            0, 1, 49, 76, 105, 127, 150, 176, 186, 207, 220, 234, 248, 254, 261, 266, 281, 292, 304, 311, 321, 331, 341, 349, 358, 366, 376, 384, 395, 403, 410, 414, 417, 427, 433, 439, 445, 452, 457, 466, 476, 482, 488, 495, 498, 501, 506, 510, 514, 517, 519, 522, 525, 527, 530, 533, 536, 541, 544, 548, 550, 552, 553, 555, 657, 559, 561, 563, 565, 567, 569, 571, 573, 574, 576, 577, 579, 581, 582, 584, 585, 586, 586, 588, 589, 590, 590, 591, 592, 593, 594, 594, 595, 595, 596, 596, 597, 597, 598, 598, 599, 599, 600, 600, 600, 601, 601, 601, 602, 602, 602, 603, 603, 603, 604, 604, 604
    };

    /*
    Задачи:
    1. Подсвечивать итем суры при нажатии на содержание
    2. Подсвечивать итем суры при перелистывании с одной суры на другую
     */

    public DrawerQuranContentAdapter(Context context, List<QuranItemContent> quranDrawer, ViewPager2 quranPager) {
        this.quranDrawer = quranDrawer;
        this.inflater = LayoutInflater.from(context);
        this.quranPager = quranPager;
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
            int targetPage = numPageSures[position];
            quranPager.setCurrentItem(targetPage, true);
            //System.out.println(numPageSures.length);
            //System.out.println(numPageSures[position]);
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