package raf.tabiin.qurantajweed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
        if (bookmarks != null && !bookmarks.isEmpty()) {
            viewPager.post(() -> viewPager.setCurrentItem(bookmarks.get(0).getPosition(), false));
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
        sures = new String[] {
                "Сура 1. Аль-Фатиха — Открывающая",
                "Сура 2. Аль-Бакара — Корова",
                "Сура 3. Али Имран — Семейство Имрана",
                "Сура 4. Ан-Ниса — Женщины",
                "Сура 5. Аль-Маида — Трапеза",
                "Сура 6. Аль-Ан'ам — Скот",
                "Сура 7. Аль-А'раф — Преграды",
                "Сура 8. Аль-Анфаль — Трофеи",
                "Сура 9. Ат-Тауба — Покаяние",
                "Сура 10. Юнус — Юнус (Иона)",
                "Сура 11. Худ — Худ",
                "Сура 12. Йусуф — Йусуф (Иосиф)",
                "Сура 13. Ар-Ра'д — Гром",
                "Сура 14. Ибрахим — Ибрахим (Авраам)",
                "Сура 15. Аль-Хиджр — Каменное плато",
                "Сура 16. Ан-Нахль — Пчелы",
                "Сура 17. Аль-Исра — Ночной перенос",
                "Сура 18. Аль-Кахф — Пещера",
                "Сура 19. Марьям — Марьям (Мария)",
                "Сура 20. Та-Ха — Та-Ха",
                "Сура 21. Аль-Анбия — Пророки",
                "Сура 22. Аль-Хадж — Паломничество",
                "Сура 23. Аль-Му'минун — Верующие",
                "Сура 24. Ан-Нур — Свет",
                "Сура 25. Аль-Фуркан — Различение",
                "Сура 26. Аш-Шуара — Поэты",
                "Сура 27. Ан-Намль — Муравьи",
                "Сура 28. Аль-Касас — Рассказы",
                "Сура 29. Аль-Анкабут — Паук",
                "Сура 30. Ар-Рум — Римляне",
                "Сура 31. Лукман — Лукман",
                "Сура 32. Ас-Саджда — Поклон",
                "Сура 33. Аль-Ахзаб — Союзники",
                "Сура 34. Саба — Сабейцы",
                "Сура 35. Фатыр — Творец",
                "Сура 36. Йа Син — Йа Син",
                "Сура 37. Ас-Саффат — Выстраивающиеся в ряды",
                "Сура 38. Сад — Буква Сад",
                "Сура 39. Аз-Зумар — Толпы",
                "Сура 40. Гафир — Прощающий",
                "Сура 41. Фуссылят — Разъяснены",
                "Сура 42. Аш-Шура — Совет",
                "Сура 43. Аз-Зухруф — Украшения",
                "Сура 44. Ад-Духан — Дым",
                "Сура 45. Аль-Джасия — Коленопреклоненные",
                "Сура 46. Аль-Ахкаф — Дюны",
                "Сура 47. Мухаммад — Мухаммад",
                "Сура 48. Аль-Фатх — Победа",
                "Сура 49. Аль-Худжурат — Комнаты",
                "Сура 50. Каф — Буква Каф",
                "Сура 51. Аль-Дариат — Рассеивающие",
                "Сура 52. Ат-Тур — Гора",
                "Сура 53. Ан-Наджм — Звезда",
                "Сура 54. Аль-Камар — Луна",
                "Сура 55. Ар-Рахман — Милостивый",
                "Сура 56. Аль-Вакы'а — Неотвратимое событие",
                "Сура 57. Аль-Хадид — Железо",
                "Сура 58. Аль-Муджадала — Препирающаяся",
                "Сура 59. Аль-Хашр — Сбор",
                "Сура 60. Аль-Мумтахина — Испытуемая",
                "Сура 61. Ас-Сафф — Ряд",
                "Сура 62. Аль-Джуму'а — Пятница",
                "Сура 63. Аль-Мунафиюн — Лицемеры",
                "Сура 64. Ат-Тагабун — Раскрытие самообмана",
                "Сура 65. Ат-Талак — Развод",
                "Сура 66. Ат-Тахрим — Запрещение",
                "Сура 67. Аль-Мульк — Власть",
                "Сура 68. Аль-Калам — Письменная трость",
                "Сура 69. Аль-Хакка — Неизбежное",
                "Сура 70. Аль-Ма'аридж — Ступени",
                "Сура 71. Нух — Нух (Ной)",
                "Сура 72. Аль-Джинн — Джинны",
                "Сура 73. Аль-Муззаммиль — Закутавшийся",
                "Сура 74. Аль-Муддассир — Завернувшийся",
                "Сура 75. Аль-Кыяма — Воскресение",
                "Сура 76. Аль-Инсан — Человек",
                "Сура 77. Аль-Мурсалят — Посланные",
                "Сура 78. Ан-Наба — Весть",
                "Сура 79. Ан-Назиат — Вырывающие",
                "Сура 80. Абаса — Нахмурившийся",
                "Сура 81. Ат-Таквир — Скручивание",
                "Сура 82. Аль-Инфитар — Раскалывание",
                "Сура 83. Аль-Мутаффифин — Обвешивающие",
                "Сура 84. Аль-Иншикак — Разверзнется",
                "Сура 85. Аль-Бурудж — Созвездия",
                "Сура 86. Ат-Тарик — Ночной путник",
                "Сура 87. Аль-А'ля — Высочайший",
                "Сура 88. Аль-Гашия — Покрывающее",
                "Сура 89. Аль-Фаджр — Заря",
                "Сура 90. Аль-Баляд — Город",
                "Сура 91. Аш-Шамс — Солнце",
                "Сура 92. Аль-Ляйл — Ночь",
                "Сура 93. Ад-Духа — Утро",
                "Сура 94. Аш-Шарх — Раскрытие",
                "Сура 95. Ат-Тин — Инжир",
                "Сура 96. Аль-'Аляк — Сгусток крови",
                "Сура 97. Аль-Кадр — Ночь предопределения",
                "Сура 98. Аль-Баййина — Ясное знамение",
                "Сура 99. Аз-Зальзаля — Землетрясение",
                "Сура 100. Аль-'Адият — Скачущие",
                "Сура 101. Аль-Кари'а — Великое бедствие",
                "Сура 102. Ат-Такасур — Страсть к приумножению",
                "Сура 103. Аль-'Аср — Предвечернее время",
                "Сура 104. Аль-Хумаза — Хулитель",
                "Сура 105. Аль-Филь — Слон",
                "Сура 106. Курайш — Курайшиты",
                "Сура 107. Аль-Ма'ун — Мелочь",
                "Сура 108. Аль-Кяусар — Изобилие",
                "Сура 109. Аль-Кяфирун — Неверующие",
                "Сура 110. Ан-Наср — Победа",
                "Сура 111. Аль-Ляхаб — Пальмовые волокна",
                "Сура 112. Аль-Ихляс — Искренность",
                "Сура 113. Аль-Фаляк — Рассвет",
                "Сура 114. Ан-Нас — Люди"
        };
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