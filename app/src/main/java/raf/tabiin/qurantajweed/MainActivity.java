package raf.tabiin.qurantajweed;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import raf.tabiin.qurantajweed.adapters.BookmarkAdapter;
import raf.tabiin.qurantajweed.adapters.DrawerQuranContentAdapter;
import raf.tabiin.qurantajweed.adapters.ImageAdapter;
import raf.tabiin.qurantajweed.databinding.ActivityMainBinding;
import raf.tabiin.qurantajweed.model.Bookmark;
import raf.tabiin.qurantajweed.model.QuranItemContent;
import raf.tabiin.qurantajweed.utils.BookmarksPref;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BookmarkAdapter bookmarkAdapter;
    private int currentPosition = 0;

    private static final String PREFS_NAME = "LastPagePrefs";
    private static final String LAST_PAGE_KEY = "last_page_key";
    ActivityMainBinding b;

    private ImageButton btnStartPause;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String audioUrl = "https://archive.org/details/quran__by--mashary-al3afasy---128-kb----604-part-full-quran-604-page--safahat-/Page";

    private Integer[] numPageSures = new Integer[]{1, 2, 50, 77, 106, 128, 151, 177, 187, 208, 221, 235, 249, 255, 262, 267, 282, 293, 305, 312, 322, 332, 342, 350, 359, 367, 377, 385, 396, 404, 411, 415, 418, 428, 434, 440, 446, 453, 458, 467, 477, 483, 489, 496, 499, 502, 507, 511, 515, 518, 520, 523, 526, 528, 531, 534, 537, 542, 545, 549, 551, 553, 554, 556, 568, 560, 562, 564, 566, 568, 570, 572, 574, 575, 577, 578, 580, 582, 583, 585, 586, 587, 587, 589, 590, 591, 591, 592, 593, 594, 595, 595, 596, 596, 597, 597, 598, 598, 599, 599, 600, 600, 601, 601, 601, 602, 602, 602, 603, 603, 603, 604, 604, 604, 605};
    private String[] sures = new String[115];
    private ArrayList<QuranItemContent> suresName = new ArrayList<QuranItemContent>();
    // Создайте экземпляр BottomSheetBehavior для управления BottomSheet


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        copyJSONFromAssets();

        setSupportActionBar(b.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");


        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(b.translateBottomSheet);

        viewPager = b.viewPager;
        bookmarkAdapter = new BookmarkAdapter(this, viewPager);
        viewPager.setLayoutDirection(ViewPager2.LAYOUT_DIRECTION_RTL);

        // Use ImageAdapter for ViewPager2
        ImageAdapter imageAdapter = new ImageAdapter(this, 604, viewPager);
        viewPager.setAdapter(imageAdapter);

        btnStartPause = b.btnStartPause;
        final SeekBar seekBar = b.seekBar;
        b.playerLayout.setVisibility(View.INVISIBLE);

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
        if (bookmarks != null && !bookmarks.isEmpty()) {//bookmarks.get(0).getPosition()
            viewPager.post(() -> viewPager.setCurrentItem(bookmarkAdapter.getCurrentPosition(), false));
        }

        initContent();
        initMap();

        DrawerQuranContentAdapter drawerQuranContentAdapter = new DrawerQuranContentAdapter(this, suresName, viewPager);
        RecyclerView quranContent = b.quranDrawerContent;
        quranContent.setAdapter(drawerQuranContentAdapter);
        quranContent.setHasFixedSize(true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                saveLastPage(position);
                try {
                    // Обновление иконки закладки для текущей страницы
                    Menu menu = b.toolbar.getMenu();
                    MenuItem addBookmarkItem = menu.findItem(R.id.action_add_bookmark);
                    updateBookmarkIcon(position, addBookmarkItem);
                    currentPosition = position;
                    bookmarkAdapter.setCurrentPosition(position);
                } catch (Exception e) {
                    // Вывод исключения в лог для отладки
                    e.printStackTrace();
                }
                b.tafsirText.setText(loadTextFromFile(position+1 + ".txt"));
                b.scrollTafsir.scrollTo(0, 0);
                bookmarkAdapter.notifyDataSetChanged();
            }
        });

        int lastPage = loadLastPage();
        viewPager.setCurrentItem(lastPage, false);
        if (bookmarkAdapter.isBookmarked(lastPage)) {
            Menu menu = b.toolbar.getMenu();
            MenuItem menuItem = menu.findItem(R.id.action_add_bookmark);
            menuItem.setIcon(R.drawable.bookmark_full);
        }

        b.goToPage.setOnClickListener(v -> {
            goToPageAlert();
        });

        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        boolean isCollapsed = true;

        b.translateQuran.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                // Если BottomSheet свернут, то развернуть его
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.close_svg);
            } else {
                // Если BottomSheet развернут или находится в состоянии перетаскивания, то свернуть его
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.open_svg);
            }
        });

        b.imOpenCloseTafsir.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                // Если BottomSheet свернут, то развернуть его
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.close_svg);
            } else {
                // Если BottomSheet развернут или находится в состоянии перетаскивания, то свернуть его
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.open_svg);
            }
        });

        // Настройка слушателя событий для bottomSheet
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    // Если bottomSheet находится в состоянии перетаскивания, блокируем его сворачивание свайпом вниз
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Ничего не делаем при изменении состояния сворачивания/разворачивания
                //TODO
            }
        });

        // Настройка слушателя событий для ScrollView
        b.scrollTafsir.setOnTouchListener((v, event) -> {
            // Обработка касания на прокрутку ScrollView
            // Возвращаем true, чтобы указать, что событие было обработано и не передавать его дальше
            return false;
        });

        b.playQuran.setOnClickListener(v -> {
            if (b.playerLayout.getVisibility() == View.VISIBLE) {
                b.playerLayout.setVisibility(View.INVISIBLE);
            } else {
                b.playerLayout.setVisibility(View.VISIBLE);
            }
        });

        btnStartPause.setOnClickListener(v -> {
            if (!isPlaying) {
                // Проверка на наличие интернет-подключения
                if (!isInternetConnected()) {
                    Snackbar.make(v, "Нет подключения к интернету", Snackbar.LENGTH_SHORT).show();
                } else {

                    // Начать проигрывание
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(getAudioUrlForCurrentPage());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                                isPlaying = true;
                                btnStartPause.setImageResource(R.drawable.pause);
                            }
                        });
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Поставить на паузу
                mediaPlayer.pause();
                isPlaying = false;
                btnStartPause.setImageResource(R.drawable.play);
            }
            //Snackbar.make(v, String.format("%03d", currentPosition) + ".mp3", Snackbar.LENGTH_SHORT).show();

        });


    }

    private void goToPageAlert() {

        MaterialAlertDialogBuilder alert =
                new MaterialAlertDialogBuilder(this);

        View dialogView = getLayoutInflater()
                .inflate(R.layout.go_to_page_dialog, null);

        alert.setTitle("Перейти на страницу");
        alert.setMessage("введите страницу");
        alert.setCancelable(true);

        EditText pageNum = dialogView.findViewById(R.id.pageNum);

        alert.setNegativeButton("Отмена", (dialogInterface, i) -> {

        });


        alert.setPositiveButton("Перейти", (dialogInterface, i) -> {
            if (pageNum.getText().toString().length() == 0) {
                Snackbar.make(b.getRoot(), "Ничего не введено. Введите текстведите номер страницы", Snackbar.LENGTH_SHORT).show();
            } else {
                int page = Integer.parseInt(pageNum.getText().toString()
                        .replaceAll("[\\.\\-,\\s]+", ""));
                if ((page < 1) || (page > 604) || (pageNum.getText().toString().isEmpty())) {
                    Snackbar.make(b.getRoot(), "В Коране 604 страницы. Введите номер от 1 до 604", Snackbar.LENGTH_SHORT).show();
                } else {
                    viewPager.setCurrentItem(page - 1, true);
                }
            }
        });

        alert.setView(dialogView);
        alert.show();

    }

    // Проверка на наличие интернет-подключения
    private boolean isInternetConnected() {
        // Реализуйте вашу логику проверки подключения к интернету здесь
        return true; // Верните true, если есть подключение, и false в противном случае
    }

    // Получение URL аудиофайла для текущей страницы
    private String getAudioUrlForCurrentPage() {
        int currentPage = 1; // Здесь должен быть код для получения текущей страницы ViewPager
        return audioUrl + String.format("%03d", currentPage) + ".mp3";
    }

    private void initContent() {
        sures = new String[] {
                "Сура 1. Аль-Фатиха\nОткрывающая",
                "Сура 2. Аль-Бакара\nКорова",
                "Сура 3. Али Имран\nСемейство Имрана",
                "Сура 4. Ан-Ниса\nЖенщины",
                "Сура 5. Аль-Маида\nТрапеза",
                "Сура 6. Аль-Ан'ам\nСкот",
                "Сура 7. Аль-А'раф\nПреграды",
                "Сура 8. Аль-Анфаль\nТрофеи",
                "Сура 9. Ат-Тауба\nПокаяние",
                "Сура 10. Юнус\nЮнус (Иона)",
                "Сура 11. Худ\nХуд",
                "Сура 12. Йусуф\nЙусуф (Иосиф)",
                "Сура 13. Ар-Ра'д\nГром",
                "Сура 14. Ибрахим\nИбрахим (Авраам)",
                "Сура 15. Аль-Хиджр\nКаменное плато",
                "Сура 16. Ан-Нахль\nПчелы",
                "Сура 17. Аль-Исра\nНочной перенос",
                "Сура 18. Аль-Кахф\nПещера",
                "Сура 19. Марьям\nМарьям (Мария)",
                "Сура 20. Та-Ха\nТа-Ха",
                "Сура 21. Аль-Анбия\nПророки",
                "Сура 22. Аль-Хадж\nПаломничество",
                "Сура 23. Аль-Му'минун\nВерующие",
                "Сура 24. Ан-Нур\nСвет",
                "Сура 25. Аль-Фуркан\nРазличение",
                "Сура 26. Аш-Шуара\nПоэты",
                "Сура 27. Ан-Намль\nМуравьи",
                "Сура 28. Аль-Касас\nРассказы",
                "Сура 29. Аль-Анкабут\nПаук",
                "Сура 30. Ар-Рум\nРимляне",
                "Сура 31. Лукман\nЛукман",
                "Сура 32. Ас-Саджда\nПоклон",
                "Сура 33. Аль-Ахзаб\nСоюзники",
                "Сура 34. Саба\nСабейцы",
                "Сура 35. Фатыр\nТворец",
                "Сура 36. Йа Син\nЙа Син",
                "Сура 37. Ас-Саффат\nВыстраивающиеся в ряды",
                "Сура 38. Сад\nБуква Сад",
                "Сура 39. Аз-Зумар\nТолпы",
                "Сура 40. Гафир\nПрощающий",
                "Сура 41. Фуссылят\nРазъяснены",
                "Сура 42. Аш-Шура\nСовет",
                "Сура 43. Аз-Зухруф\nУкрашения",
                "Сура 44. Ад-Духан\nДым",
                "Сура 45. Аль-Джасия\nКоленопреклоненные",
                "Сура 46. Аль-Ахкаф\nДюны",
                "Сура 47. Мухаммад\nМухаммад",
                "Сура 48. Аль-Фатх\nПобеда",
                "Сура 49. Аль-Худжурат\nКомнаты",
                "Сура 50. Каф\nБуква Каф",
                "Сура 51. Аль-Дариат\nРассеивающие",
                "Сура 52. Ат-Тур\nГора",
                "Сура 53. Ан-Наджм\nЗвезда",
                "Сура 54. Аль-Камар\nЛуна",
                "Сура 55. Ар-Рахман\nМилостивый",
                "Сура 56. Аль-Вакы'а\nНеотвратимое событие",
                "Сура 57. Аль-Хадид\nЖелезо",
                "Сура 58. Аль-Муджадала\nПрепирающаяся",
                "Сура 59. Аль-Хашр\nСбор",
                "Сура 60. Аль-Мумтахина\nИспытуемая",
                "Сура 61. Ас-Сафф\nРяд",
                "Сура 62. Аль-Джуму'а\nПятница",
                "Сура 63. Аль-Мунафикун\nЛицемеры",
                "Сура 64. Ат-Тагабун\nРаскрытие самообмана",
                "Сура 65. Ат-Талак\nРазвод",
                "Сура 66. Ат-Тахрим\nЗапрещение",
                "Сура 67. Аль-Мульк\nВласть",
                "Сура 68. Аль-Калам\nПисьменная трость",
                "Сура 69. Аль-Хакка\nНеизбежное",
                "Сура 70. Аль-Ма'аридж\nСтупени",
                "Сура 71. Нух\nНух (Ной)",
                "Сура 72. Аль-Джинн\nДжинны",
                "Сура 73. Аль-Муззаммиль\nЗакутавшийся",
                "Сура 74. Аль-Муддассир\nЗавернувшийся",
                "Сура 75. Аль-Кыяма\nВоскресение",
                "Сура 76. Аль-Инсан\nЧеловек",
                "Сура 77. Аль-Мурсалят\nПосланные",
                "Сура 78. Ан-Наба\nВесть",
                "Сура 79. Ан-Назиат\nВырывающие",
                "Сура 80. Абаса\nНахмурившийся",
                "Сура 81. Ат-Таквир\nСкручивание",
                "Сура 82. Аль-Инфитар\nРаскалывание",
                "Сура 83. Аль-Мутаффифин\nОбвешивающие",
                "Сура 84. Аль-Иншикак\nРазверзнется",
                "Сура 85. Аль-Бурудж\nСозвездия",
                "Сура 86. Ат-Тарик\nНочной путник",
                "Сура 87. Аль-А'ля\nВысочайший",
                "Сура 88. Аль-Гашия\nПокрывающее",
                "Сура 89. Аль-Фаджр\nЗаря",
                "Сура 90. Аль-Баляд\nГород",
                "Сура 91. Аш-Шамс\nСолнце",
                "Сура 92. Аль-Ляйл\nНочь",
                "Сура 93. Ад-Духа\nУтро",
                "Сура 94. Аш-Шарх\nРаскрытие",
                "Сура 95. Ат-Тин\nИнжир",
                "Сура 96. Аль-'Аляк\nСгусток крови",
                "Сура 97. Аль-Кадр\nНочь предопределения",
                "Сура 98. Аль-Баййина\nЯсное знамение",
                "Сура 99. Аз-Зальзаля\nЗемлетрясение",
                "Сура 100. Аль-'Адият\nСкачущие",
                "Сура 101. Аль-Кари'а\nВеликое бедствие",
                "Сура 102. Ат-Такасур\nСтрасть к приумножению",
                "Сура 103. Аль-'Аср\nПредвечернее время",
                "Сура 104. Аль-Хумаза\nХулитель",
                "Сура 105. Аль-Филь\nСлон",
                "Сура 106. Курайш\nКурайшиты",
                "Сура 107. Аль-Ма'ун\nМелочь",
                "Сура 108. Аль-Кяусар\nИзобилие",
                "Сура 109. Аль-Кяфирун\nНеверующие",
                "Сура 110. Ан-Наср\nПобеда",
                "Сура 111. Аль-Ляхаб\nПальмовые волокна",
                "Сура 112. Аль-Ихляс\nИскренность",
                "Сура 113. Аль-Фаляк\nРассвет",
                "Сура 114. Ан-Нас\nЛюди"
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

    // Метод для сохранения последней страницы в SharedPreferences
    private void saveLastPage(int lastPage) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LAST_PAGE_KEY, lastPage);
        editor.apply();
    }

    // Метод для загрузки последней страницы из SharedPreferences
    private int loadLastPage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(LAST_PAGE_KEY, 0); // По умолчанию начнем с 0 страницы
    }

    private void copyJSONFromAssets() {
        AssetManager assetManager = getAssets();
        File outFile = new File(getFilesDir(), "bookmarks.json");

        // Проверяем, существует ли файл уже во внутреннем хранилище
        if (!outFile.exists()) {
            try {
                InputStream in = assetManager.open("bookmarks.json");
                OutputStream out = new FileOutputStream(outFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSeekBarUpdateThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSeekBarUpdateThread();
    }

    // Метод для запуска потока обновления SeekBar
    private void startSeekBarUpdateThread() {
        final SeekBar seekBar = findViewById(R.id.seekBar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    try {
                        final int currentPosition = mediaPlayer.getCurrentPosition();
                        final int totalDuration = mediaPlayer.getDuration();
                        seekBar.setProgress(currentPosition);
                        seekBar.setMax(totalDuration);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Метод для остановки потока обновления SeekBar
    private void stopSeekBarUpdateThread() {
        // Здесь можно добавить логику остановки потока, если это необходимо
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

                bookmarkAdapter.notifyDataSetChanged();

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

    private String loadTextFromFile(String fileName) {
        StringBuilder text = new StringBuilder();
        try {
            InputStream is = getAssets().open("tafsir_al_muntahab/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public interface OnPageChangedListener {
        void onPageChanged(int position);
        int getCurrentPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}