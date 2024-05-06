package raf.tabiin.qurantajweed.details;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.adapters.BookmarkAdapter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarkAdapter bookmarkAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.bookmarkRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //bookmarkAdapter = new BookmarkAdapter(this, );
        recyclerView.setAdapter(bookmarkAdapter);

        // Здесь также можно добавить обработку кликов на элементы списка, если нужно
    }
}