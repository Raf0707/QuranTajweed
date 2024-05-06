package raf.tabiin.qurantajweed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import raf.tabiin.qurantajweed.adapters.BookmarkAdapter;
import raf.tabiin.qurantajweed.adapters.DrawerQuranContentAdapter;
import raf.tabiin.qurantajweed.adapters.ImageAdapter;
import raf.tabiin.qurantajweed.databinding.ActivityMainBinding;
import raf.tabiin.qurantajweed.details.BookmarkActivity;
import raf.tabiin.qurantajweed.model.Bookmark;
import raf.tabiin.qurantajweed.model.QuranItemContent;
import raf.tabiin.qurantajweed.utils.BookmarksPref;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BookmarkAdapter bookmarkAdapter;
    private int currentPosition = 0;

    private BookmarksPref bookmarksPref;
    ActivityMainBinding b;

    private Integer[] numPageSures = new Integer[]{1, 2, 50, 77, 106, 128, 151, 177, 187, 208, 221, 325, 249, 255, 262, 267, 282, 293, 305, 312, 322, 332, 342, 350, 359, 367, 377, 385, 396, 404, 411, 415, 418, 428, 434, 440, 446, 453, 458, 467, 477, 483, 489, 496, 499, 502, 507, 511, 515, 518, 520, 523, 526, 528, 531, 534, 537, 542, 545, 549, 551, 553, 554, 556, 658, 560, 562, 564, 566, 568, 570, 572, 574, 575, 577, 578, 580, 582, 583, 585, 586, 587, 587, 589, 590, 591, 591, 592, 593, 594, 595, 595, 596, 596, 597, 597, 598, 598, 599, 599, 600, 600, 601, 601, 601, 602, 602, 602, 603, 603, 603, 604, 604, 604, 605};
    private String[] sures = new String[115];
    private ArrayList<QuranItemContent> suresName = new ArrayList<QuranItemContent>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        b.drawerQuranLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);

        bookmarksPref = new BookmarksPref(this);

        viewPager = b.viewPager;
        bookmarkAdapter = new BookmarkAdapter(this, viewPager);
        viewPager.setLayoutDirection(ViewPager2.LAYOUT_DIRECTION_RTL);

        // Use ImageAdapter for ViewPager2
        ImageAdapter imageAdapter = new ImageAdapter(this, 604, viewPager);
        viewPager.setAdapter(imageAdapter);

        Menu menu = b.toolbar.getMenu();
        MenuItem addBookmarkItem = menu.findItem(R.id.action_add_bookmark);

        imageAdapter.setOnPageChangedListener(new OnPageChangedListener() {
            @Override
            public void onPageChanged(int position) {
                // Handle ViewPager2 position change
                // Here you can pass the position wherever you need
                currentPosition = position;
            }

            @Override
            public int getCurrentPosition() {
                return currentPosition;
            }
        });

        RecyclerView bookMarksRecycle = b.bookmarksQuranRecycle;
        bookMarksRecycle.setAdapter(bookmarkAdapter);
        bookMarksRecycle.setHasFixedSize(false);

        bookmarkAdapter.loadBookmarks();

        // Set current position from bookmarks if available
        List<Bookmark> bookmarks = bookmarkAdapter.getBookmarks();
        /*if (bookmarks != null && !bookmarks.isEmpty()) {
            viewPager.post(() -> viewPager.setCurrentItem(bookmarks.get(0).getPosition(), false));
        }*/

        if (bookmarks != null && !bookmarks.isEmpty()) {
            int initialPage = bookmarks.iterator().next().getPosition();
            viewPager.setCurrentItem(initialPage, false);
        }

        initContent();
        initMap();

        DrawerQuranContentAdapter drawerQuranContentAdapter = new DrawerQuranContentAdapter(this, suresName, viewPager);
        RecyclerView quranContent = b.quranDrawerContent;
        quranContent.setAdapter(drawerQuranContentAdapter);
        quranContent.setHasFixedSize(false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                try {
                    // Обновление иконки закладки для текущей страницы
                    Menu menu = b.toolbar.getMenu();
                    MenuItem addBookmarkItem = menu.findItem(R.id.action_add_bookmark);
                    updateBookmarkIcon(position, addBookmarkItem);
                } catch (Exception e) {
                    // Вывод исключения в лог для отладки
                    e.printStackTrace();
                }
            }
        });

    }

    private void initContent() {
        // Initialize array
        sures[0] = "1. «Аль-Фатиха» \n («Открывающая Книгу»)";
        sures[1] = "2. «Аль-Бакара» \n («Корова»)";
        sures[2] = "3. «Аль-Имран» \n («Семейство Имрана»)";
        sures[3] = "4. «Ан-Ниса» \n («Женщины»)";
        sures[4] = "5. «Аль-Маида» \n («Трапеза»)";
        sures[5] = "6. «Аль-Анам» \n («Скот»)";
        sures[6] = "7. «Аль-Араф» \n («Преграды»)";
        sures[7] = "8. «Аль-Анфаль» \n («Трофеи»)";
        sures[8] = "9. «Ат-Тауба» \n («Покаяние»)";
        sures[9] = "10. «Йунус» \n («Иона»)";
        sures[10] = "11. «Худ» \n («Худ»)";
        sures[11] = "12. «Юсуф» \n («Юсуф»)";
        sures[12] = "13. «Ар-Ра’д» \n («Гром»)";
        sures[13] = "14. «Ибрахим» \n («Абрахам»)";
        sures[14] = "15. «Аль-Хиджр» \n («Хиджр»)";
        sures[15] = "16. «Ан-Наль» \n («Пчелы»)";
        sures[16] = "17. «Аль-Исра» \n («Ночной перенос»)";
        sures[17] = "18. «Аль-Кахф» \n («Пещера»)";
        sures[18] = "19. «Марьям» \n («Мария»)";
        sures[19] = "20. «Таха» \n («Та Ха»)";
        sures[20] = "21. «Аль-Анбиа» \n («Пророки»)";
        sures[21] = "22. «Аль-Хадид» \n («Железо»)";
        sures[22] = "23. «Аль-Муджадила» \n («Онемевшая»)";
        sures[23] = "24. «Аль-Хашр» \n («Сбор»)";
        sures[24] = "25. «Аль-Мумтагина» \n («Испытуемые»)";
        sures[25] = "26. «Ан-Намль» \n («Муравьи»)";
        sures[26] = "27. «Аль-Касас» \n («Рассказ»)";
        sures[27] = "28. «Аль-Анкабут» \n («Паук»)";
        sures[28] = "29. «Ар-Рум» \n («Римляне»)";
        sures[29] = "30. «Лукман» \n («Лукман»)";
        sures[30] = "31. «Ас-Саджда» \n («Поклонение»)";
        sures[31] = "32. «Аль-Ахзаб» \n («Союзники»)";
        sures[32] = "33. «Саба» \n («Сава»)";
        sures[33] = "34. «Фатыр» \n («Творец»)";
        sures[34] = "35. «Ясин» \n («Ясин»)";
        sures[35] = "36. «Ас-Саффат» \n («Саффат»)";
        sures[36] = "37. «Сад» \n («Сад»)";
        sures[37] = "38. «Аз-Зумар» \n («Толпы»)";
        sures[38] = "39. «Гафир» \n («Прощение»)";
        sures[39] = "40. «Фуссилат» \n («Разъяснены»)";
        sures[40] = "41. «Аш-Шура» \n («Помещение»)";
        sures[41] = "42. «Аз-Зухруф» \n («Украшения»)";
        sures[42] = "43. «Ад-Духан» \n («Дым»)";
        sures[43] = "44. «Аль-Джазия» \n («Золото»)";
        sures[44] = "45. «Аль-Ахкаф» \n («Барханы»)";
        sures[45] = "46. «Аль-Ахкаф» \n («Создание»)";
        sures[46] = "47. «Мухаммад» \n («Мухаммад»)";
        sures[47] = "48. «Аль-Фатх» \n («Победа»)";
        sures[48] = "49. «Аль-Худжурат» \n («Комнаты»)";
        sures[49] = "50. «Каф» \n («Каф»)";
        sures[50] = "51. «Аз-Зарият» \n («Рассеивающие»)";
        sures[51] = "52. «Ат-Тур» \n («Гора»)";
        sures[52] = "53. «Ан-Наджм» \n («Звезда»)";
        sures[53] = "54. «Аль-Камар» \n («Луна»)";
        sures[54] = "55. «Ар-Рахман» \n («Милостивый»)";
        sures[55] = "56. «Аль-Вакиа» \n («Неизбежное»)";
        sures[56] = "57. «Аль-Хадид» \n («Железо»)";
        sures[57] = "58. «Аль-Муджадила» \n («Онемевшая»)";
        sures[58] = "59. «Аль-Хашр» \n («Сбор»)";
        sures[59] = "60. «Аль-Мумтагина» \n («Испытуемые»)";
        sures[60] = "61. «Ас-Сафф» \n («Ряд»)";
        sures[61] = "62. «Аль-Джумуа» \n («Пятница»)";
        sures[62] = "63. «Аль-Мунафикун» \n («Лицемеры»)";
        sures[63] = "64. «Ат-Тагабун» \n («Обделение»)";
        sures[64] = "65. «Ат-Таляк» \n («Развод»)";
        sures[65] = "66. «Ат-Тахрим» \n («Запрещение»)";
        sures[66] = "67. «Аль-Мульк» \n («Царство»)";
        sures[67] = "68. «Аль-Калам» \n («Перо»)";
        sures[68] = "69. «Аль-Хакка» \n («Неизбежное»)";
        sures[69] = "70. «Аль-Маарид» \n («Ступени»)";
        sures[70] = "71. «Нух» \n («Ной»)";
        sures[71] = "72. «Аль-Джинн» \n («Джинны»)";
        sures[72] = "73. «Аль-Муззаммиль» \n («Завернутый»)";
        sures[73] = "74. «Аль-Муддассир» \n («Завернувшийся в одежду»)";
        sures[74] = "75. «Аль-Кийама» \n («Воскрешение»)";
        sures[75] = "76. «Ад-Дахар» \n («Человек»)";
        sures[76] = "77. «Аль-Мурсалат» \n («Посланные»)";
        sures[77] = "78. «Ан-Наба» \n («Весть»)";
        sures[78] = "79. «Ан-Назиат» \n («Вырывающие»)";
        sures[79] = "80. «Абаса» \n («Он нахмурился»)";
        sures[80] = "81. «Ат-Таквир» \n («Сворачивание»)";
        sures[81] = "82. «Аль-Инфитар» \n («Разрушение»)";
        sures[82] = "83. «Аль-Мутаффифин» \n («Обвешивающие»)";
        sures[83] = "84. «Аль-Иншикак» \n («Разверзнется»)";
        sures[84] = "85. «Аль-Бурудж» \n («Созвездия»)";
        sures[85] = "86. «Ат-Тарик» \n («Ночной гость»)";
        sures[86] = "87. «Аль-Аля» \n («Всевышний»)";
        sures[87] = "88. «Аль-Гашия» \n («Покрывающее»)";
        sures[88] = "89. «Аль-Фаджр» \n («Заря»)";
        sures[89] = "90. «Аль-Балад» \n («Город»)";
        sures[90] = "91. «Аш-Шамс» \n («Солнце»)";
        sures[91] = "92. «Аль-Лайл» \n («Ночь»)";
        sures[92] = "93. «Ад-Духа» \n («Утро»)";
        sures[93] = "94. «Аль-Инширох» \n («Раскрытие»)";
        sures[94] = "95. «Ат-Тин» \n («Смоковница»)";
        sures[95] = "96. «Аль-Аляк» \n («Кровопийца»)";
        sures[96] = "97. «Аль-Кадр» \n («Сила»)";
        sures[97] = "98. «Аль-Баййина» \n («Ясность»)";
        sures[98] = "99. «Аз-Залзала» \n («Сотрясение»)";
        sures[99] = "100. «Аль-Адият» \n («Наездницы»)";
        sures[100] = "101. «Аль-Кариа» \n («Страшный»)";
        sures[101] = "102. «Ат-Такатур» \n («Стремление к изобилию»)";
        sures[102] = "103. «Аль-Аср» \n («Время»)";
        sures[103] = "104. «Аль-Хумаза» \n («Заговорщик»)";
        sures[104] = "105. «Аль-Филь» \n («Слон»)";
        sures[105] = "106. «Курайш» \n («Курайшиты»)";
        sures[106] = "107. «Аль-Маун» \n («Помощь»)";
        sures[107] = "108. «Аль-Каусар» \n («Изобилие»)";
        sures[108] = "109. «Аль-Кафирун» \n («Неверные»)";
        sures[109] = "110. «Ан-Наср» \n («Помощь»)";
        sures[110] = "111. «Аль-Масад» \n («Хлыст»)";
        sures[111] = "112. «Аль-Ихлас» \n («Очищение»)";
        sures[112] = "113. «Аль-Фалак» \n («Рассвет»)";
        sures[113] = "114. «Ан-Нас» \n («Люди»)";
    }

    private void initMap() {
        for (int i = 0; i < 114; i++) {
            QuranItemContent sure = new QuranItemContent(sures[i], numPageSures[i]);
            suresName.add(sure);
        }
    }

    public void updateBookmarkIcon(int page, MenuItem menuItem) {
        try {
            // Проверка наличия закладки на текущей странице
            boolean isBookmarked = bookmarkAdapter.isBookmarked(page);

            if (isBookmarked) {
                // Если на текущей странице есть закладка, установите значок bookmark_full
                menuItem.setIcon(R.drawable.bookmark_full);
            } else {
                // Если закладки нет, установите значок bookmark_empty
                menuItem.setIcon(R.drawable.bookmark_empty);
            }
        } catch (Exception e) {
            // Вывод исключения в лог для отладки
            e.printStackTrace();
        }
    }

    public void toggleBookmark(int page) {
        if (bookmarkAdapter.isBookmarked(page)) {
            bookmarkAdapter.removeBookmark(page);
        } else {
            bookmarkAdapter.addBookmark(new Bookmark(page));
        }
        // Обновите значок закладки
        Menu menu = b.toolbar.getMenu();
        MenuItem addBookmarkItem = menu.findItem(R.id.action_add_bookmark);
        updateBookmarkIcon(page, addBookmarkItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_bookmark:
                int currentPosition = bookmarkAdapter.getCurrentPosition();
                boolean isBookmarked = bookmarkAdapter.isBookmarked(currentPosition);

                if (isBookmarked) {
                    // Если страница уже в закладках, удалите закладку
                    bookmarkAdapter.removeBookmark(currentPosition);
                    // Установите иконку bookmark_empty
                    item.setIcon(R.drawable.bookmark_empty);
                } else {
                    // Если страница не в закладках, добавьте закладку
                    Bookmark bookmark = new Bookmark(currentPosition);
                    bookmarkAdapter.addBookmark(bookmark);
                    // Установите иконку bookmark_full
                    item.setIcon(R.drawable.bookmark_full);
                }
                return true;
            case R.id.action_view_bookmarks:
                b.drawerQuranLayout.closeDrawer(Gravity.LEFT);
                b.drawerQuranLayout.openDrawer(Gravity.RIGHT);
                return true;
            case R.id.list_content:
                b.drawerQuranLayout.closeDrawer(Gravity.RIGHT);
                b.drawerQuranLayout.openDrawer(Gravity.LEFT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnPageChangedListener {
        void onPageChanged(int position);
        int getCurrentPosition();
    }

}