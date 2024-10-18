package raf.tabiin.qurantajweed;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import raf.tabiin.qurantajweed.adapters.BookmarkAdapter;
import raf.tabiin.qurantajweed.adapters.DrawerQuranContentAdapter;
import raf.tabiin.qurantajweed.adapters.ImageAdapter;
import raf.tabiin.qurantajweed.adapters.MusicAdapter;
import raf.tabiin.qurantajweed.databinding.ActivityMainBinding;
import raf.tabiin.qurantajweed.model.Bookmark;
import raf.tabiin.qurantajweed.model.QuranItemContent;
import raf.tabiin.qurantajweed.ui.container.ConteinerActivity;
import raf.tabiin.qurantajweed.ui.details.tutorials.TutorialTafsirPanelActivity;
import raf.tabiin.qurantajweed.ui.player.players.SoulPlayer;
import raf.tabiin.qurantajweed.utils.AsyncHttpClient;
import raf.tabiin.qurantajweed.ui.player.res_downloaders.DownloadFilesTask;
import raf.tabiin.qurantajweed.ui.player.res_downloaders.MailRuDownloader;
import raf.tabiin.qurantajweed.utils.OnSwipeTouchListener;
import raf.tabiin.qurantajweed.ui.player.res_downloaders.QuranAudioZipDownloader;

public class MainActivity extends AppCompatActivity implements AsyncHttpClient.DownloadListener, SensorEventListener {

    private static final String ZIP_FILE_NAME = "QuranPagesAudio.zip";
    private static final int TOTAL_FILES_COUNT = 604;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;

    private ViewPager2 viewPager;
    private BookmarkAdapter bookmarkAdapter;
    private int currentPosition = 0;

    private MediaPlayer mediaPlayer;
    private List<File> audioFiles;
    private int currentFileIndex = 0;

    private static final String PREFS_NAME = "LastPagePrefs";
    private static final String LAST_PAGE_KEY = "last_page_key";
    ActivityMainBinding b;
    String audioUrl = "https://ia801605.us.archive.org/3/items/quran__by--mashary-al3afasy---128-kb----604-part-full-quran-604-page--safahat-/Page";

    private ImageButton btnStartPause;
    private MediaPlayer zipMediaPlayer;
    private boolean isPlaying = false;
    private Integer[] numPageSures = new Integer[]{1, 2, 50, 77, 106, 128, 151, 177, 187, 208, 221, 235, 249, 255, 262, 267, 282, 293, 305, 312, 322, 332, 342, 350, 359, 367, 377, 385, 396, 404, 411, 415, 418, 428, 434, 440, 446, 453, 458, 467, 477, 483, 489, 496, 499, 502, 507, 511, 515, 518, 520, 523, 526, 528, 531, 534, 537, 542, 545, 549, 551, 553, 554, 556, 568, 560, 562, 564, 566, 568, 570, 572, 574, 575, 577, 578, 580, 582, 583, 585, 586, 587, 587, 589, 590, 591, 591, 592, 593, 594, 595, 595, 596, 596, 597, 597, 598, 598, 599, 599, 600, 600, 601, 601, 601, 602, 602, 602, 603, 603, 603, 604, 604, 604, 605};
    private String[] sures = new String[115];
    public SoulPlayer soulPlayer;
    private ArrayList<QuranItemContent> suresName = new ArrayList<QuranItemContent>();
    // Создайте экземпляр BottomSheetBehavior для управления BottomSheet

    private AsyncHttpClient asyncHttpClient;
    private MailRuDownloader mailRuDownloader;
    public MediaPlayer quranSevenHours;
    private Handler handler;
    private Runnable updateSeekBarRunnable;
    private static final String PREFS_AUDIO_NAME = "QuranAudioPrefs";
    private static final String KEY_SEEK_POSITION = "seek_position";

    private QuranAudioZipDownloader quranAudioZipDownloader;

    private static final float SHAKE_THRESHOLD = 15.0f; // Пороговое значение ускорения
    private Sensor accelerometer;
    private long lastUpdate;

    String selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Значение по умолчанию

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        copyJSONFromAssets();

        setSupportActionBar(b.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        asyncHttpClient = new AsyncHttpClient();

        mailRuDownloader = new MailRuDownloader(getApplicationContext());

        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(b.translateBottomSheet);

        showRandomQuoteOnLaunch();

        viewPager = b.viewPager;
        bookmarkAdapter = new BookmarkAdapter(this, viewPager);
        viewPager.setLayoutDirection(ViewPager2.LAYOUT_DIRECTION_RTL);

        // Use ImageAdapter for ViewPager2
        ImageAdapter imageAdapter = new ImageAdapter(this, 604, viewPager);
        viewPager.setAdapter(imageAdapter);

        btnStartPause = b.btnStartPause;
        b.playerLayout.setVisibility(View.INVISIBLE);

        quranSevenHours = MediaPlayer.create(MainActivity.this, R.raw.quran_7_hours);
        b.seekBar.setMax(quranSevenHours.getDuration());

        // Восстанавливаем прогресс
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedPosition = prefs.getInt(KEY_SEEK_POSITION, 0);
        quranSevenHours.seekTo(savedPosition);
        b.seekBar.setProgress(savedPosition);
        handler = new Handler();


        lastUpdate = System.currentTimeMillis();

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
        // Инициализация по умолчанию при запуске приложения
        b.tafsirDictQuranRadioGroup.check(R.id.tafsirRadioButton); // Установить taфsirRadioButton как выбранный по умолчанию
        loadTafsirText(); // Загрузить текст при запуске

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
                b.pageNumberText.setText(String.valueOf(position+1));
                b.tafsirText.setText(loadTextFromFile(position+1 + ".txt"));
                System.out.println(position+1 + ".txt");
                //TODO Выводить название суры
                b.scrollTafsir.scrollTo(0, 0);
                b.tafsirDictQuranRadioGroup.check(R.id.tafsirRadioButton); // Установить taфsirRadioButton как выбранный по умолчанию
                loadTafsirText(); // Загрузить текст при запуске

                // Установка слушателя для tafsirRadioGroup
                b.tafsirRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Определяем директорию в зависимости от выбора в tafsirRadioGroup
                        switch (checkedId) {
                            case R.id.abuAliAshary:
                                selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Директория для первого элемента
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                            case R.id.alMuntahab:
                                selectedTafsirDirectory = "tafsir_al_muntahab"; // Директория для второго элемента
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                            case R.id.dumrf:
                                selectedTafsirDirectory = "tafsir_dum_rf"; // Директория для третьего элемента
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                            default:
                                selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Значение по умолчанию
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                        }

                        // Загружаем текст из выбранной директории
                        b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
                    }
                });
                bookmarkAdapter.notifyDataSetChanged();
                //TODO
                if (isPlaying) {
                    //playAudio(position, );
                }
                //TODO
            }
        });

        b.forwardPageTafsir.setOnClickListener(v -> {
            /*currentPosition += 1;

            if (currentPosition < viewPager.getAdapter().getItemCount()) {
                // Устанавливаем ViewPager на следующую страницу
                viewPager.setCurrentItem(currentPosition, true);

                // Выполняем ту же логику, что и в onPageSelected
                saveLastPage(currentPosition);
                b.pageNumberText.setText(String.valueOf(currentPosition + 1));
                b.tafsirText.setText(loadTextFromFile(currentPosition + 1 + ".txt"));
                b.scrollTafsir.scrollTo(0, 0);

                // Не закрываем bottomSheet
            }*/

            int position;
            if (currentPosition + 1 >= 604) return;
            else position = currentPosition + 1;
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
            b.pageNumberText.setText(String.valueOf(position+1));
            b.tafsirText.setText(loadTextFromFile(position+1 + ".txt"));
            System.out.println(position+1 + ".txt");
            //TODO Выводить название суры
            b.scrollTafsir.scrollTo(0, 0);
            b.tafsirDictQuranRadioGroup.check(R.id.tafsirRadioButton); // Установить taфsirRadioButton как выбранный по умолчанию
            loadTafsirText(); // Загрузить текст при запуске
            viewPager.setCurrentItem(position, true);

            // Установка слушателя для tafsirRadioGroup
            b.tafsirRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // Определяем директорию в зависимости от выбора в tafsirRadioGroup
                    switch (checkedId) {
                        case R.id.abuAliAshary:
                            selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Директория для первого элемента
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                        case R.id.alMuntahab:
                            selectedTafsirDirectory = "tafsir_al_muntahab"; // Директория для второго элемента
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                        case R.id.dumrf:
                            selectedTafsirDirectory = "tafsir_dum_rf"; // Директория для третьего элемента
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                        default:
                            selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Значение по умолчанию
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                    }

                    // Загружаем текст из выбранной директории
                    b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
                }
            });
            bookmarkAdapter.notifyDataSetChanged();
        });

        b.backPageTafsir.setOnClickListener(v -> {
            /*currentPosition -= 1;

            if (currentPosition >= 0) {
                // Устанавливаем ViewPager на предыдущую страницу
                viewPager.setCurrentItem(currentPosition, true);

                // Выполняем ту же логику, что и в onPageSelected
                saveLastPage(currentPosition);
                b.pageNumberText.setText(String.valueOf(currentPosition + 1));
                b.tafsirText.setText(loadTextFromFile(currentPosition + 1 + ".txt"));
                b.scrollTafsir.scrollTo(0, 0);

                // Не закрываем bottomSheet
            }*/
            int position;
            if (currentPosition - 1 < 1) return;
            else position = currentPosition - 1;
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
            b.pageNumberText.setText(String.valueOf(position+1));
            b.tafsirText.setText(loadTextFromFile(position+1 + ".txt"));
            System.out.println(position+1 + ".txt");
            //TODO Выводить название суры
            b.scrollTafsir.scrollTo(0, 0);
            b.tafsirDictQuranRadioGroup.check(R.id.tafsirRadioButton); // Установить taфsirRadioButton как выбранный по умолчанию
            loadTafsirText(); // Загрузить текст при запуске
            viewPager.setCurrentItem(position, true);

            // Установка слушателя для tafsirRadioGroup
            b.tafsirRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // Определяем директорию в зависимости от выбора в tafsirRadioGroup
                    switch (checkedId) {
                        case R.id.abuAliAshary:
                            selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Директория для первого элемента
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                        case R.id.alMuntahab:
                            selectedTafsirDirectory = "tafsir_al_muntahab"; // Директория для второго элемента
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                        case R.id.dumrf:
                            selectedTafsirDirectory = "tafsir_dum_rf"; // Директория для третьего элемента
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                        default:
                            selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Значение по умолчанию
                            loadTafsirText(); // Загрузить текст при запуске
                            break;
                    }

                    // Загружаем текст из выбранной директории
                    b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
                }
            });
            bookmarkAdapter.notifyDataSetChanged();
        });


        b.tafsirDictQuranRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String directory = ""; // Переменная для хранения директории
                b.tafsirRadioGroup.setVisibility(View.VISIBLE); // Скрываем tafsirRadioGroup по умолчанию

                // Проверяем, какой элемент RadioButton был выбран
                if (checkedId == R.id.dictRadioButton) {
                    // Если выбран dictRadioButton, устанавливаем директорию для словаря
                    directory = "quran_dict";
                    b.tafsirRadioGroup.setVisibility(View.GONE); // Скрываем tafsirRadioGroup
                    // Загружаем текст для словаря
                    b.tafsirText.setText(loadTextFromFile(directory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
                } else if (checkedId == R.id.tafsirRadioButton) {
                    // Если выбран tafsirRadioButton, показываем tafsirRadioGroup
                    b.tafsirRadioGroup.setVisibility(View.VISIBLE);
                    // Загружаем текст из выбранной директории тафсира при первом запуске
                    b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
                }

                // Установка слушателя для tafsirRadioGroup
                b.tafsirRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Определяем директорию в зависимости от выбора в tafsirRadioGroup
                        switch (checkedId) {
                            case R.id.abuAliAshary:
                                selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Директория для первого элемента
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                            case R.id.alMuntahab:
                                selectedTafsirDirectory = "tafsir_al_muntahab"; // Директория для второго элемента
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                            case R.id.dumrf:
                                selectedTafsirDirectory = "tafsir_dum_rf"; // Директория для третьего элемента
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                            default:
                                selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Значение по умолчанию
                                loadTafsirText(); // Загрузить текст при запуске
                                break;
                        }

                        // Загружаем текст из выбранной директории
                        b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
                    }
                });
            }
        });

// Инициализация по умолчанию при запуске приложения
        b.tafsirDictQuranRadioGroup.check(R.id.tafsirRadioButton); // Установить taфsirRadioButton как выбранный по умолчанию
        loadTafsirText(); // Загрузить текст при запуске

        // Установка слушателя для tafsirRadioGroup
        b.tafsirRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Определяем директорию в зависимости от выбора в tafsirRadioGroup
                switch (checkedId) {
                    case R.id.abuAliAshary:
                        selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Директория для первого элемента
                        loadTafsirText(); // Загрузить текст при запуске
                        break;
                    case R.id.alMuntahab:
                        selectedTafsirDirectory = "tafsir_al_muntahab"; // Директория для второго элемента
                        loadTafsirText(); // Загрузить текст при запуске
                        break;
                    case R.id.dumrf:
                        selectedTafsirDirectory = "tafsir_dum_rf"; // Директория для третьего элемента
                        loadTafsirText(); // Загрузить текст при запуске
                        break;
                    default:
                        selectedTafsirDirectory = "tafsir_abu_ali_al_ashary"; // Значение по умолчанию
                        loadTafsirText(); // Загрузить текст при запуске
                        break;
                }

                // Загружаем текст из выбранной директории
                b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
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

        b.settingsAppFragment.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConteinerActivity.class);
            startActivity(intent);
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
            }
        });

        b.translateBottomSheet.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            @Override
            public void onDoubleClick() {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Если BottomSheet свернут, то развернуть его
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    b.imOpenCloseTafsir.setImageResource(R.drawable.close_svg);
                    //Snackbar.make(b.getRoot(), "Нажмите дважды, чтобы закрыть", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Если BottomSheet развернут или находится в состоянии перетаскивания, то свернуть его
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    b.imOpenCloseTafsir.setImageResource(R.drawable.open_svg);
                    //Snackbar.make(b.getRoot(), "Нажмите дважды, чтобы открыть", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeDown() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.open_svg);
                //Snackbar.make(b.getRoot(), "Свайп вверх от кнопки управления - открыть", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeUp() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.close_svg);
                //Snackbar.make(b.getRoot(), "Свайп вниз от кнопки управления - закрыть", Snackbar.LENGTH_SHORT).show();
            }
        });

        b.tafsirText.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            @Override
            public void onDoubleClick() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                b.imOpenCloseTafsir.setImageResource(R.drawable.open_svg);
            }
        });

        b.playQuran.setOnClickListener(v -> {
            if (b.playerLayout.getVisibility() == View.VISIBLE) {
                b.playerLayout.setVisibility(View.INVISIBLE);
                savePlayerVisibilityState(false); // Сохранить состояние - скрыт
            } else {
                b.playerLayout.setVisibility(View.VISIBLE);
                savePlayerVisibilityState(true); // Сохранить состояние - видим
            }
        });

        //MusicPlayer musicPlayer = new MusicPlayer(getApplicationContext(), b.seekBar);
        //SoulPlayer player = new SoulPlayer(getApplicationContext(), b.seekBar, b.btnStartPause, b.btnLoopMode);
        /*updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (quranSevenHours != null && quranSevenHours.isPlaying()) {
                    int currentPosition = quranSevenHours.getCurrentPosition();
                    b.seekBar.setProgress(currentPosition);
                    handler.postDelayed(this, 1000); // Обновляем каждую секунду
                }
            }
        };*/

        soulPlayer = new SoulPlayer(getApplicationContext(), b.seekBar, b.btnStartPause,
                b.btnLoopMode);

        btnStartPause.setOnClickListener(v -> {
            if (soulPlayer.isPlaying()) {
                soulPlayer.pause();
                btnStartPause.setImageResource(R.drawable.play);
            } else if (soulPlayer.isPaused()) {
                soulPlayer.resume();
                btnStartPause.setImageResource(R.drawable.pause);
            } else {
                String audioUrl = getAudioUrlForCurrentPage();
                soulPlayer.playAudioWithStreaming(audioUrl);
                btnStartPause.setImageResource(R.drawable.pause);
            }
            MusicAdapter adapter;
            adapter = new MusicAdapter(getApplicationContext(), getAudioUrlForCurrentPage(), i -> {
                soulPlayer.play(i.data);
            });

        });


        b.infoOpenCloseTafsir.setOnClickListener(v -> {
            infoGesture();
        });

    }
    private void goToPageAlert() {

        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.go_to_page_dialog, null);

        alert.setCancelable(true);

        EditText pageNum = dialogView.findViewById(R.id.pageNum);
        TextView suraTitle = dialogView.findViewById(R.id.suraTitleAyats);
        AutoCompleteTextView suraClassic = dialogView.findViewById(R.id.suraClassic);
        AutoCompleteTextView ayatClassic = dialogView.findViewById(R.id.ayatClassic);
        MaterialButton back = dialogView.findViewById(R.id.backPage);
        MaterialButton forward = dialogView.findViewById(R.id.forwardPage);
        Slider pageSlider = dialogView.findViewById(R.id.pageSlider);

        TextInputLayout suraInputLayout = dialogView.findViewById(R.id.suraInputLayout);
        TextInputLayout ayatInputLayout = dialogView.findViewById(R.id.ayatInputLayout);

        String[] options = new String[] {"Страница", "Сура", "Джуз", "Хизб"};

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                options
        );

        ArrayAdapter<String> suresTitleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                sures
        );

        suraClassic.setCursorVisible(false);
        suraClassic.setFocusableInTouchMode(false);

        ayatClassic.setCursorVisible(false);
        ayatClassic.setFocusableInTouchMode(false);

        suraClassic.setOnItemClickListener((parent, view, position, id) -> {
            // Получаем выбранное имя суры
            String selectedSuraName = (String) parent.getItemAtPosition(position);

            // Находим индекс выбранной суры в оригинальном списке suraNames
            int suraIndex = -1;
            for (int i = 0; i < sures.length; i++) {
                if (sures[i].equals(selectedSuraName)) {
                    suraIndex = i + 1; // +1 для получения реального номера суры
                    break;
                }
            }

            // Проверяем, что индекс найден
            if (suraIndex != -1) {
                // Заменяем текст на индекс суры
                suraClassic.setText(String.valueOf(suraIndex), false);

                // Обновляем ayatAutoComplete с использованием getNumAyatsOfSure(suraIndex)
                int numAyats = bookmarkAdapter.getNumAyatsOfSure(suraIndex);
                String[] ayatArray = new String[numAyats];
                for (int i = 0; i < numAyats; i++) {
                    ayatArray[i] = String.valueOf(i + 1);
                }

                ArrayAdapter<String> ayatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ayatArray);
                ayatClassic.setAdapter(ayatAdapter);

            } else {
                // Обработка ошибки (если необходимо)
                Snackbar.make(b.getRoot(), "Ошибка: выбранная сура не найдена", Snackbar.LENGTH_SHORT).show();
            }
        });

        suraClassic.setAdapter(suresTitleAdapter);

        back.setOnClickListener(view -> {
            int mcurrentPage = viewPager.getCurrentItem() + 1;
            if (pageNum.getText().toString().isEmpty()) {
                pageNum.setText(String.valueOf(mcurrentPage));
                pageSlider.setValue(mcurrentPage);
            } else {
                int page = Integer.parseInt(pageNum.getText().toString());
                if (page > 1) {
                    pageNum.setText(String.valueOf(page - 1));
                    pageSlider.setValue(page - 1);
                }
            }
        });

        forward.setOnClickListener(view -> {
            int mcurrentPage = viewPager.getCurrentItem() + 1;
            if (pageNum.getText().toString().isEmpty()) {
                pageNum.setText(String.valueOf(mcurrentPage));
                pageSlider.setValue(mcurrentPage);
            } else {
                int page = Integer.parseInt(pageNum.getText().toString());
                if (page < 604) {
                    pageNum.setText(String.valueOf(page + 1));
                    pageSlider.setValue(page + 1);
                }
            }
        });

        TextWatcher suraAyatWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String suraInput = suraClassic.getText().toString().replaceAll("[\\.\\-,\\s]+", "");
                String ayatInput = ayatClassic.getText().toString().replaceAll("[\\.\\-,\\s]+", "");
                if (ayatClassic.getText().toString().isEmpty()) ayatInput = "1";

                int sura, ayat, page;
                if (!suraInput.isEmpty() && !ayatInput.isEmpty()) {
                    try {
                        sura = Integer.parseInt(suraInput);
                        ayat = Integer.parseInt(ayatInput);
                    } catch (NumberFormatException e) {
                        pageNum.setText("");
                        return;
                    }

                    int numAyats = bookmarkAdapter.getNumAyatsOfSure(sura);
                    if (!ayatInput.isEmpty()) {
                        try {
                            ayat = Integer.parseInt(ayatInput);
                            if (ayat > numAyats) {
                                // Если выбранный аят больше чем количество аятов в суре, очищаем поле
                                ayatClassic.setText("");
                                Snackbar.make(ayatClassic, "Выбранный аят не существует в этой суре", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            pageNum.setText("");
                            return;
                        }
                    } else {
                        // Если аят не указан, устанавливаем значение по умолчанию - 1
                        ayat = 1;
                    }

                    page = bookmarkAdapter.goToAyat(sura, ayat);
                    if (page != -1) {
                        pageNum.setText(String.valueOf(page));
                        suraTitle.setText(bookmarkAdapter.getSuraTitle(page) + ",\n" + bookmarkAdapter.getAyatsOnPage(page));
                        pageSlider.setValue(page);

                    } else if (!suraInput.isEmpty() && ayatInput.isEmpty()) {
                        try {
                            sura = Integer.parseInt(suraInput);
                            ayat = 1;
                        } catch (NumberFormatException e) {
                            pageNum.setText("");
                            return;
                        }
                        page = bookmarkAdapter.goToAyat(sura, ayat);
                        if (page != -1) {
                            pageNum.setText(String.valueOf(page));
                            suraTitle.setText(bookmarkAdapter.getSuraTitle(page) + ",\n" + bookmarkAdapter.getAyatsOnPage(page));
                        }
                    } else {
                        pageNum.setText("");
                    }
                } else {
                    pageNum.setText("");
                }
            }
        };

        suraClassic.addTextChangedListener(suraAyatWatcher);
        ayatClassic.addTextChangedListener(suraAyatWatcher);

        pageNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().replaceAll("[\\.\\-,\\s]+", "");
                if (!input.isEmpty()) {
                    int page;
                    try {
                        page = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        suraTitle.setText("");
                        return;
                    }
                    if (page >= 1 && page <= 604) {
                        suraTitle.setText(bookmarkAdapter.getSuraTitle(page) + ",\n" + bookmarkAdapter.getAyatsOnPage(page));
                        pageSlider.setValue(page);
                    } else {
                        suraTitle.setText("");
                    }
                } else {
                    suraTitle.setText("");
                }
            }
        });


        /*pageSlider.addOnChangeListener((slider, value, fromUser) -> {
                // Обновляем значение других компонентов на основе pageSlider
                int page = (int) value;
                pageNum.setText(String.valueOf(page));
                suraTitle.setText(bookmarkAdapter.getSuraTitle(page) + ",\n" + bookmarkAdapter.getAyatsOnPage(page));
                suraClassic.setText(String.valueOf(bookmarkAdapter.getSuraNum(page)));
                ayatClassic.setText("1");

        });*/

        pageSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
                // Начало взаимодействия, ничего не делаем
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                int page = (int) slider.getValue();
                pageNum.setText(String.valueOf(page));
                suraTitle.setText(bookmarkAdapter.getSuraTitle(page) + ",\n" + bookmarkAdapter.getAyatsOnPage(page));
                //suraClassic.setText(String.valueOf(bookmarkAdapter.getSuraNum(page))); // Исправление
                //ayatClassic.setText("1");

            }
        });

        alert.setNegativeButton("Отмена", (dialogInterface, i) -> {
            // Ничего не делаем
        });

        alert.setPositiveButton("Перейти", (dialogInterface, i) -> {
            if (pageNum.getText().toString().length() == 0) {
                Snackbar.make(b.getRoot(), "Ничего не введено. Введите номер страницы", Snackbar.LENGTH_SHORT).show();
            } else {
                int page = Integer.parseInt(pageNum.getText().toString().replaceAll("[\\.\\-,\\s]+", ""));
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
    private boolean isInternetConnected(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return Objects.requireNonNull(wifi).isConnected() || Objects.requireNonNull(mobile).isConnected();
    }

    private String getAudioUrlForCurrentPage() {
        int currentPage = bookmarkAdapter.getCurrentPosition() + 1; // Здесь должен быть код для получения текущей страницы ViewPager
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

    private void savePlayerVisibilityState(boolean isVisible) {
        SharedPreferences preferences = getSharedPreferences("PlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("player_visible", isVisible);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSeekBarUpdateThread();
        SharedPreferences preferences = getSharedPreferences("PlayerPrefs", Context.MODE_PRIVATE);
        boolean isPlayerVisible = preferences.getBoolean("player_visible", false);
        if (isPlayerVisible) {
            b.playerLayout.setVisibility(View.VISIBLE);
        } else {
            b.playerLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSeekBarUpdateThread();
        SharedPreferences preferences = getSharedPreferences("PlayerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("player_visible", b.playerLayout.getVisibility() == View.VISIBLE);
        editor.apply();
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

    /*private String loadTextFromFile(String fileName) {
        StringBuilder text = new StringBuilder();
        try {
            //InputStream is = getAssets().open("tafsir_al_muntahab/" + fileName);
            InputStream is = getAssets().open("tafsir_abu_ali_al_ashary/" + fileName);
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

    private String loadTextFromFile(String directory, String fileName) {
        StringBuilder text = new StringBuilder();
        try {
            InputStream is = getAssets().open(directory + "/" + fileName);
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
    }*/

    // Метод для загрузки текста тафсира
    private void loadTafsirText() {
        b.tafsirText.setText(loadTextFromFile(selectedTafsirDirectory, bookmarkAdapter.getCurrentPosition() + 1 + ".txt"));
    }
    private String loadTextFromFile(String fileName) {
        StringBuilder text = new StringBuilder();
        InputStream is = null;
        BufferedReader br = null;

        try {
            // Открытие файла из папки assets
            is = getAssets().open("tafsir_abu_ali_al_ashary/" + fileName);
            br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }

        } catch (IOException e) {
            Log.e("loadTextFromFile", "Ошибка при открытии файла: " + fileName, e);
        } finally {
            // Закрытие потоков в блоке finally
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e("loadTextFromFile", "Ошибка при закрытии потоков: " + fileName, e);
            }
        }

        return text.toString();
    }

    private String loadTextFromFile(String directory, String fileName) {
        StringBuilder text = new StringBuilder();
        InputStream is = null;
        BufferedReader br = null;

        try {
            // Открытие файла из указанной директории
            is = getAssets().open(directory + "/" + fileName);
            br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }

        } catch (IOException e) {
            Log.e("loadTextFromFile", "Ошибка при открытии файла: " + fileName, e);
        } finally {
            // Закрытие потоков в блоке finally
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e("loadTextFromFile", "Ошибка при закрытии потоков: " + fileName, e);
            }
        }

        return text.toString();
    }




    @Override
    public void onSuccess(File file) {

    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onProgress(int progress) {

    }

    private void openDocumentTree() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri treeUri = data.getData();
                // Grant permissions to the content URI
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // Save the URI for later use
                saveTreeUri(treeUri);

                // Call the method to check and prepare audio files
                checkAndPrepareQuranAudioFiles(treeUri);
            }
        }
    }



    private void saveTreeUri(Uri uri) {
        SharedPreferences sharedPreferences = getSharedPreferences("QuranAudio", MODE_PRIVATE);
        sharedPreferences.edit().putString("treeUri", uri.toString()).apply();
    }

    private Uri getSavedTreeUri() {
        SharedPreferences sharedPreferences = getSharedPreferences("QuranAudio", MODE_PRIVATE);
        String uriString = sharedPreferences.getString("treeUri", null);
        return uriString != null ? Uri.parse(uriString) : null;
    }


    private void listFilesInDirectory(Uri treeUri) {
        if (treeUri == null) {
            return;
        }

        DocumentFile directory = DocumentFile.fromTreeUri(this, treeUri);
        if (directory != null && directory.isDirectory()) {
            for (DocumentFile file : directory.listFiles()) {
                // Process each file in the directory
                Log.d("File", "File name: " + file.getName());
            }
        }
    }



    private boolean areQuranAudioFilesDownloaded(DocumentFile directory) {
        for (int i = 1; i <= 604; i++) {
            String fileName = String.format("%03d.mp3", i);
            DocumentFile audioFile = directory.findFile(fileName);
            if (audioFile == null || !audioFile.exists()) {
                return false;
            }
        }
        return true;
    }



    private void unzipFile(DocumentFile zipFile, DocumentFile targetDirectory) throws IOException {
        InputStream is = getContentResolver().openInputStream(zipFile.getUri());
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry zipEntry = zis.getNextEntry();
        byte[] buffer = new byte[1024];
        while (zipEntry != null) {
            DocumentFile newFile = targetDirectory.createFile("application/octet-stream", zipEntry.getName());
            OutputStream fos = getContentResolver().openOutputStream(newFile.getUri());
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private void checkAndPrepareQuranAudioFiles(Uri treeUri) {
        DocumentFile directory = DocumentFile.fromTreeUri(this, treeUri);
        if (directory == null || !directory.isDirectory()) {
            Snackbar.make(findViewById(android.R.id.content), "Ошибка доступа к директории", Snackbar.LENGTH_LONG).show();
            return;
        }

        DocumentFile zipFile = null;
        for (DocumentFile file : directory.listFiles()) {
            if (file.getName().equals(ZIP_FILE_NAME) && file.isFile()) {
                zipFile = file;
                break;
            }
        }

        if (areQuranAudioFilesDownloaded(directory)) {
            // Все файлы найдены, начинаем проигрывание
            int currentPage = viewPager.getCurrentItem(); // Получаем текущую позицию ViewPager2
            playAudio(currentPage, directory);
        } else if (zipFile != null) {
            // Архив найден в папке загрузок, но не распакован
            try {
                unzipFile(zipFile, directory);
                if (areQuranAudioFilesDownloaded(directory)) {
                    // Если файлы успешно распакованы, начинаем проигрывание
                    int currentPage = viewPager.getCurrentItem(); // Получаем текущую позицию ViewPager2
                    playAudio(currentPage, directory);
                } else {
                    // Обработка ошибки распаковки
                    Snackbar.make(findViewById(android.R.id.content), "Ошибка распаковки архива", Snackbar.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки распаковки
                Snackbar.make(findViewById(android.R.id.content), "Ошибка распаковки архива", Snackbar.LENGTH_LONG).show();
            }
        } else {
            // Архив не найден, переходим к скачиванию
            showDownloadQuranAudioAlert();
        }
    }


    private boolean isDownloading = false; // Переменная для отслеживания состояния загрузки

    private void showDownloadQuranAudioAlert() {
        if (isDownloading) {
            // Если уже идет загрузка, показываем Snackbar
            Snackbar.make(findViewById(android.R.id.content), "Дождитесь полной загрузки архива", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Создаем прогрессбар
        final ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true);

        // Создаем диалоговое окно
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Download Quran Audio")
                .setMessage("Some audio files are missing. Do you want to download them?")
                .setView(progressBar)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDownloading = false; // Сбрасываем флаг загрузки при отмене
                    }
                })
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDownloading) {
                            // Если уже идет загрузка, показываем Snackbar
                            Snackbar.make(findViewById(android.R.id.content), "Дождитесь полной загрузки архива", Snackbar.LENGTH_LONG).show();
                        } else {
                            // Запускаем процесс загрузки всех файлов или недостающих файлов
                            isDownloading = true; // Устанавливаем флаг загрузки
                            QuranAudioZipDownloader quranAudioDownloader = new QuranAudioZipDownloader(getApplicationContext());

                            // Указываем путь назначения для загрузки файлов
                            String downloadUrl = "https://cloclo.datacloudmail.ru/zip64/DZ6ueuwdIYgeqe69ZXjB01Tvi42JqWINYggsDWCh6USi6hUb0Q0a2a8kZt/QuranPagesAudio.zip";

                            // Запускаем загрузку файлов
                            quranAudioDownloader.downloadFiles(downloadUrl, new DownloadCallback() {
                                @Override
                                public void onDownloadComplete() {
                                    isDownloading = false; // Сбрасываем флаг загрузки
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Обновляем UI после завершения загрузки
                                            checkAndPrepareQuranAudioFiles(getSavedTreeUri()); // Проверяем и готовим аудиофайлы
                                        }
                                    });
                                }

                                @Override
                                public void onDownloadError(String error) {
                                    isDownloading = false; // Сбрасываем флаг загрузки в случае ошибки
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Обновляем UI после ошибки загрузки
                                            Snackbar.make(findViewById(android.R.id.content), "Ошибка загрузки: " + error, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });

                            // Отображаем уведомление о начале загрузки
                            quranAudioDownloader.showDownloadNotification();
                        }
                    }
                });

        // Создаем и отображаем диалоговое окно
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Отображаем количество скачанных файлов над прогрессбаром
        final TextView progressText = new TextView(this);
        progressText.setText("0 / 604");
        builder.setView(progressText);

        // Поток для обновления прогрессбара и текста
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isDownloading) {
                    // Получаем количество скачанных файлов
                    int downloadedFiles = getDownloadedFilesCount();
                    // Обновляем текст и прогрессбар
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress(downloadedFiles, progressBar, progressText);
                        }
                    });

                    // Проверяем, завершено ли скачивание
                    if (downloadedFiles == 604) {
                        // Если все файлы скачаны, выводим сообщение "готово"
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setMessage("Download complete!");
                            }
                        });
                        isDownloading = false;
                        break;
                    }

                    try {
                        // Задержка перед следующей проверкой
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void showQuoteDialog(String quote) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Читайте Коран! Любите Коран!")
                .setMessage(quote)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private final String[] quotes = {
            "Аллах не наказывает человека, хранящего в сердце Коран\n" +
                    "\n" +
                    "Абу Амама аль-Бахили: «Читайте Коран, и пусть вас не вводят в обман эти собранные вместе листы, ибо Аллах не наказывает сердце, которое хранит Коран (то есть сердце того, кто выучил его и выполняет его установления, а тот, кто выучил его слова, но не следует им, не является его хранителем)».",
            "Главные знания можно найти в Коране\n" +
                    "\n" +
                    "Ибн Масъуд, да будет доволен им Аллах: «Если хотите постичь знания, то ищите их в Коране, ибо  в нем – знание первых и последних (людей)».",
            "Вознаграждение обещано за каждую букву Корана\n" +
                    "\n" +
                    "Ибн Масъуд, да будет доволен им Аллах, передал: «Читайте Коран, ибо за каждую прочитанную букву вам зачитывается десять добрых дел. Я не говорю, что «алиф лям мим» - это одна буква, но что «алиф» - буква, «лям» - это буква и «мим» - это буква».",
            "По отношению к Корану можно узнать свою любовь к Аллаху и Его пророку\n" +
                    "\n" +
                    "Ибн Масъуд, да будет доволен им Аллах, сказал: «Пусть никто из вас не спрашивает себя ни о чем, кроме как о Коране, (чтобы знать, любит ли он Аллаха и Его Посланника, да благословит его Аллах и приветствует): ведь если он любит Коран и он вызывает в нем восхищение, значит, он любит Аллаха и Его Посланника, да благословит его Аллах и приветствует, а если он не любит Коран, значит, он не любит Аллаха и Его Посланника, да благословит его Аллах и приветствует».",
            "Аяты – это степени в Раю\n" +
                    "\n" +
                    "Амр бин аль-Ас, да будет доволен им Аллах, сказал: «Каждый аят Корана – это степень в Раю и светоч в ваших домах». И также: «Кто прочтет Коран, того (Аллах) возведет на степень пророчества (нубувва), разве что ему не будет ниспослано откровение свыше».",
            "Чтение Корана приносит баракат в дом\n" +
                    "\n" +
                    "Абу Хурайра, да будет доволен им Аллах, сказал: «Поистине, дом, где читают Коран, станет просторным для его обитателей, и увеличится его благодать, в него войдут ангелы и оттуда изойдут дьяволы, а дом, где не читают книгу Аллаха, станет тесным для его обитателей, уменьшится его благодать, покинут его ангелы и поселятся там дьяволы».",
            "Всевышний любит чтение Корана как с пониманием, так и без понимания\n" +
                    "\n" +
                    "Имам Ахмад ибн Ханбал сказал: «Я увидел во сне Аллаха и спросил у Него: «О Господи, что есть лучшее из того, благодаря которому к Тебе приближаются приближенные?» Всевышний ответил: «Чтение Моего Корана, о Ахмад». Я вновь спросил: «С пониманием или без понимания?» И услышал: «С пониманием и без понимания».",
            "Знание Корана возвышает выше халифов и  других влиятельных лиц\n" +
                    "\n" +
                    "«Носителю знания Корана, поступающему согласно ему, не следует унижаться ни перед кем ради исполнения своих нужд – ни перед халифами, ни перед другими. (Напротив), следует, чтобы люди нуждались в нем».",
            "Ангелы проявляют почтение к чтецам Корана\n" +
                    "\n" +
                    "Суфьян ас-Саури сказал: «Когда человек завершает чтение Корана, ангел целует его между глаз, (проявляя к нему и к тому, что он прочитал, почтение)».",
            "В Коране – богатство, в его утрате – бедность\n" +
                    "\n" +
                    "Рассказывают, что Халид ибн Укба пришел к Посланнику Аллаха, да благословит его Аллах и приветствует, и попросил его: «Прочитай мне что-нибудь из Корана». Пророк, да благословит его Аллах и приветствует, прочел ему аят: «(Скажи): «Поистине, Аллах приказывает проявлять справедливость (утверждением единобожия, выражением благодарности Аллаха за милости), истовость в поклонение и  добродетельность в отношениях с людьми  и давать пожертвования близким» (сура «ан-Нахль», «Пчелы», аят 90).\n" +
                    "\n" +
                    "Халид попросил прочесть еще раз, и Пророк, да благословит его Аллах и приветствует, повторил аят, после чего Халид сказал: «Клянусь Аллахом, в нем сладость и изящество слога! Поистине, эта книга – как дерево, внизу в листве, а наверху усеян плодами. Такое не сказать смертному!»\n" +
                    "\n" +
                    "Также Хасан аль-Басри сказал: «Клянусь Аллахом, нет богатства превыше Корана и нет бедности хуже утраты Корана».",
            "Чтение Корана приумножает благодеяния\n" +
                    "\n" +
                    "Амр ибн Маймун сказал: «Если кто на утренней молитве, раскрыв Коран, прочтет из него сто аятов, то Аллах зачтет это для него как равное благим деяниям всех людей этого мира».",
            "Чтение Корана улучшает память\n" +
                    "\n" +
                    "Али ибн Абу Талиб, да будет доволен им Аллах, сказал: «Три вещи улучшают память и избавляют от мокроты: сивак, пост и чтение Корана».",
            "Чтение Корана, согласно высказываниям праведников, которые сделали его постоянным своим спутником, связано с многочисленными благами, как в этом мире, так и в следующей жизни. Чтение Корана приумножает мирские блага, возвышает человека в степени, украшает будущую жизнь и дает успокоение и сладость сердцу. Никакие отговорки подобно «боюсь читать неправильно», «не понимаю», «совершаю ошибки», «читаю сбивчиво», «нет времени», «в следующем году/месяце» и т.п. не являются серьезным обоснованием отказа от всех перечисленных благ. Главное – решиться читать Коран, добиваясь этим довольства Аллаха, а все остальные препятствия со временем отпадут сами собой. Как говорил имам Ахмад, Всевышний любит чтение Корана и с пониманием и без понимания. Как сказал Посланник Аллаха, да благословит его Аллах и приветствует, (смысл) за чтение Корана дается вознаграждение, а за чтение с трудностями (сбивчиво или запинаясь) – двойная награда. Чтение Корана даже без понимания успокаивает сердце, очищает душу, умножает благосостояние человека. Поэтому ориентируясь на Посланника Аллаха, да благословит его Аллах и приветствует,  и его благочестивых последователей, мы должны в этом священном месяце сделать хотя бы один шаг в сторону Корана: кто не умеет читать – научиться, кто умеет, но не читал – начать, а кто умеет и уже читал – стараться увеличить чтение и следовать установлениям книги Аллаха. Одно из лучших вложений, которое мы можем сделать в своей жизни – это вложение своих сил и времени на чтение (и следование) Корану.\n" +
                    "\n © Ася Гагиева",
            "Передаётся от Анаса ибн Малика: «Посланник Аллаха ﷺ сказал:\n" +
                    "\n" +
                    "إنَّ للَّهِ أَهْلِينَ مِنَ النَّاسِ قَالُوا يَا رَسُولَ اللَّهِ مَنْ هُمْ قَالَ هُمْ أَهْلُ الْقُرْآنِ أَهْلُ اللَّهِ وَخَاصَّتُهُ\n" +
                    "\n" +
                    "«У Аллаха есть приближённые из числа людей!». Его спросили: «О Посланник Аллаха, кто они?», — на что он ﷺ ответил: «Приверженцы Корана! Они и есть приближённые Аллаха и Его избранники» (Ибн Маджа).",
            "Передаётся от Абдуллаха ибн Масуда: «Посланник Аллаха ﷺ сказал:\n" +
                    "\n" +
                    "مَنْ قَرَأَ حَرْفًا مِنْ كِتَابِ اللَّهِ فَلَهُ بِهِ حَسَنَةٌ وَالْحَسَنَةُ بِعَشْرِ أَمْثَالِهَا لَا أَقُولُ الم حَرْفٌ وَلَكِنْ أَلِفٌ حَرْفٌ وَلَامٌ حَرْفٌ وَمِيمٌ حَرْفٌ\n" +
                    "\n" +
                    "«Тому, кто прочтёт (хотя бы одну) букву из Книги Аллаха, (запишется одно) доброе дело, а за каждое доброе дело воздастся десятикратно, и я не говорю, что «алиф, лям, мим» — это одна буква. «Алиф» — это одна буква, «лям» — другая буква и «мим» — третья буква» (ат-Тирмизи, который сказал, что хадис хасан сахих).",
            "Передаётся от Усмана ибн Аффана, что Пророк ﷺ сказал:\n" +
                    "\n" +
                    "خَيْرُكُمْ مَن تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ\n" +
                    "\n" +
                    "«Лучший из вас — кто изучает Коран и обучает ему других» (Бухари).",
            "Передаётся от Абу Умамы аль-Бахили: «Я слышал, как Посланник Аллаха ﷺ говорил:\n" +
                    "\n" +
                    "اِقْرَءُوا الْقُرْآنَ فَإِنَّهُ يَأْتِي يَوْمَ الْقِيَامَةِ شَفِيعًا لِأَصْحَابِهِ\n" +
                    "\n" +
                    "«Читайте Коран! Ведь в Судный день он явится как заступник за тех, кто читал его» (Муслим).",
            "Передаётся от Абдуллаха ибн Амра:\n" +
                    "\n" +
                    "يُقَالُ لِصَاحِبِ الْقُرْآنِ اقْرَأْ وَارْتَقِ وَرَتِّلْ كَمَا كُنْتَ تُرَتِّلُ فِي الدُّنْيَا فَإِنَّ مَنْزِلَتَكَ عِنْدَ آخِرِ آيَةٍ تَقْرَؤُهَا\n" +
                    "\n" +
                    "«Знавшему Коран в Судный день будет сказано: «Читай и подними себя. Читай Коран, как читал его в земной жизни. И место твоё будет соответствовать последнему прочтённому тобою аяту» (Ахмад).",
            "Передаётся от Джабира:\n" +
                    "\n" +
                    "كَانَ النَّبِيُّ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ يَجْمَعُ بَيْنَ الرَّجُلَيْنِ مِنْ قَتْلَى أُحَدٍ فِي ثَوْبٍ وَاحِدٍ ثُمَّ يَقُولُ أَيُّهُمْ أَكْثَرُ أَخْذًا لِلْقُرْآنِ فَيُقَدِّمُهُ فِي اللَّحْدِ\n" +
                    "\n" +
                    "«Пророк ﷺ заворачивал тела каждых двоих убитых в битве при Ухуде в один саван, а потом спрашивал: «Кто из них больше знает Коран?». Если указывали на одного из них, то он приказывал класть его в нишу первым...» (Бухари).",
            "Передаётся от Абу Хурайры, что Пророк ﷺ сказал:\n" +
                    "\n" +
                    "مَا اجْتَمَعَ قَوْمٌ فِي بَيْتٍ مِنْ بُيُوتِ اللَّهِ تَعَالَى يَتْلُونَ كِتَابَ اللَّهِ وَيَتَدَارَسُونَهُ بَيْنَهُمْ إِلاَّ نَزَلَتْ عَلَيْهِمُ السَّكِينَةُ وَغَشِيَتْهُمُ الرَّحْمَةُ وَحَفَّتْهُمُ الْمَلاَئِكَةُ وَذَكَرَهُمُ اللَّهُ فِيمَنْ عِنْدَهُ\n" +
                    "\n" +
                    "«Когда бы ни собирались люди в одном из домов Аллаха для чтения и изучения Корана, на них непременно нисходит спокойствие. Милость покрывает их, ангелы окружают их, и Аллах упоминает о них среди тех, кто у Него» (Муслим).",
            "Передаётся от Абу Хурайры, что Пророк ﷺ сказал:\n" +
                    "\n" +
                    "يَجِيءُ القُرْآنُ يَوْمَ القِيَامَةِ فَيَقُولُ يَا رَبِّ حَلِّهِ فَيُلْبَسُ تَاجَ الْكَرَامَةِ ثُمَّ يَقُولُ يَا رَبِّ زِدْهُ فَيُلْبَسُ حُلَّةَ الْكَرَامَةِ ثُمَّ يَقُولُ يَا رَبِّ ارْضَ عَنْهُ فَيَرْضَى عَنْهُ فَيُقَالُ لَهُ اقْرَأْ وَارْقَ وَيُزَادُ بِكُلِّ آيَةٍ حَسَنَةً\n" +
                    "\n" +
                    "«В Судный день придёт Коран и скажет: «О Господь, укрась его (читавшего Коран)!», — после чего на читавшего Коран наденут корону почёта. Затем Коран скажет: «О Господь, прибавь ему!», — и наденут на него одежду почёта. Затем Коран скажет: «О Господь, будь доволен им!», — и Он будет им доволен. (Затем читавшему Коран) скажут: «Читай и возвышайся!», — и за каждый аят ему прибавят (награду, как за совершение одного) благого дела!» (ат-Тирмизи, сказав, что хадис хасан).",
            "Передаётся от Абу Хурайры, что Пророк ﷺ говорил:\n" +
                    "\n" +
                    "مَا أَذِنَ اللَّهُ لِشَيْءٍ مَا أَذِنَ لِلنَّبِيِّ أَنْ يَتَغَنَّى بِالقُرْآنِ\n" +
                    "\n" +
                    "«Никому не внимал Аллах так, как внимал Пророку, когда он читал Коран нараспев» (Бухари, Муслим).",
            "Передаётся от Абу Хурайры:\n" +
                    "\n" +
                    "إِذَا قَرَأَ ابْنُ آدَمَ السَّجْدَةَ اعْتَزَلَ الشَّيْطَانُ يَبْكِي يَقُولُ يَا وَيْلَهُ أُمِرَ ابْنُ آدَمَ بِالسُّجُودِ فَسَجَدَ فَلَهُ الْجَنَّةُ وَأُمِرْتُ بِالسُّجُودِ فَأَبَيْتُ فَلِيَ النَّار\n" +
                    "\n" +
                    "«Когда сын Адама читает аят земного поклона и совершает земной поклон, Шайтан уходит с плачем, говоря: «Горе ему! Ведь сыну Адама было велено совершить земной поклон, он совершил, и ему за это — Рай, и мне было велено, но я отказался, и за это мне причитается Ад» (Муслим).",
            "Передаётся от Абу Саида аль-Худри, а он — от Муавии:\n" +
                    "\n" +
                    "أَنَّ رَسُولَ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ خَرَجَ عَلَى حَلْقَةٍ مِنْ أَصْحَابِهِ فَقَالَ مَا أَجْلَسَكُمْ قَالُوا جَلَسْنَا نَذْكُرُ اللَّهَ وَنَحْمَدُهُ عَلَى مَا هَدَانَا لِلْإِسْلَامِ وَمَنَّ بِهِ عَلَيْنَا قَالَ آللَّهِ مَا أَجْلَسَكُمْ إِلَّا ذَاكَ قَالُوا وَاللَّهِ مَا أَجْلَسَنَا إِلَّا ذَاكَ قَالَ أَمَا إِنِّي لَمْ أَسْتَحْلِفْكُمْ تُهْمَةً لَكُمْ وَلَكِنَّهُ أَتَانِي جِبْرِيلُ فَأَخْبَرَنِي أَنَّ اللَّهَ عَزَّ وَجَلَّ يُبَاهِي بِكُمُ الْمَلَائِكَةَ\n" +
                    "\n" +
                    "«Посланник Аллаха ﷺ подошёл к группе своих сподвижников, находившихся в мечети, и сказал: «Почему вы сидите?». Они ответили: «Мы сидим, поминая Аллаха и воздавая нашу благодарность Ему за то, что Он привёл нас к Исламу». Пророк Аллаха ﷺ сказал: «Вы клянётесь, что делаете именно это, сидя здесь?». Они ответили: «Клянёмся Аллахом, мы сидим здесь только поэтому». На это Пророк ﷺ сказал: «Я заставил вас поклясться не из-за того, что я сомневаюсь, а потому, что ангел Джибриль (а.с.) пришёл и сказал мне, что Аллах гордится вами перед ангелами» (Муслим).",
            "Передаётся от ан-Навваса ибн Самъана аль-Ансари:\n" +
                    "\n" +
                    "يُؤْتَى بِالْقُرْآنِ يَوْمَ الْقِيَامَةِ وَأَهْلِهِ الَّذِينَ كَانُوا يَعْمَلُونَ بِهِ تَقْدُمُهُ سُورَةُ الْبَقَرَةِ وَآلِ عِمرَانَ وَضَرَبَ لَهُمَا رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ ثَلَاثَةَ أَمْثَالٍ مَا نَسِيتُهُنَّ بَعْدُ قَالَ كَأَنَّهُمَا غَمَامَتَانِ أَوْ ظُلَّتَانِ سَوْدَاوَانِ بَيْنَهُمَا شَرْقٌ أَوْ كَأَنَّهُمَا حِزْقَانِ مِنْ طَيْرٍ صَوَافَّ تُحَاجَّانِ عَنْ صَاحِبِهِمَا\n" +
                    "\n" +
                    "«В Судный день приведут Коран и чтецов, которые следовали ему. Суры «Аль-Бакара» и «Али Имран» будут впереди. Посланник Аллаха ﷺ привёл такие три описания данных сур, что я не забыл их и по сей день: «Они словно два облака или две чёрные тени, между которыми сверкает луч света. Или они словно две стаи птиц, которые распростёрли в небе свои крылья. Эти суры охраняют людей, которые читают их» (Муслим).",
            "Передаётся от Аиши: «Посланник Аллаха ﷺ сказал:\n" +
                    "\n" +
                    "الْمَاهِرُ بِالْقُرْآنِ مَعَ السَّفَرَةِ الْكِرَامِ الْبَرَرَةِ وَالَّذِي يَقْرَأُ الْقُرْآنَ وَيَتَتَعْتَعُ فِيهِ وَهُوَ عَلَيْهِ شَاقٌّ لَهُ أَجْرَانِ\n" +
                    "\n" +
                    "«Тот, кто хорошо умеет читать Коран, будет в Раю вместе с благородными и покорными ангелами-писцами, а тому, кто читает Коран, запинаясь и испытывая при этом затруднения, уготована двойная награда» (Муслим).",
            "Передаётся от Бурайды: «Я слышал, как Пророк ﷺ говорил:\n" +
                    "\n" +
                    "إِنَّ الْقُرآنَ يَلْقَى صَاحِبَهُ يَوْمَ الْقِيَامَةِ حِينَ يَنْشَقُّ عَنْهُ قَبْرُهُ كَالرَّجُلِ الشَّاحِبِ فَيَقُولُ لَهُ هَلْ تَعْرِفُنِي فَيَقُولُ مَا أَعْرِفُكَ فَيَقُولُ أَنَا صَاحِبُكَ الْقُرْآنُ الَّذِي أَظْمَأْتُكَ فِي الْهَوَاجِرِ وَأَسْهَرْتُ لَيْلَكَ وَإِنَّ كُلَّ تَاجِرٍ مِنْ وَرَاءِ تِجَارَتِهِ وَإِنَّكَ الْيَوْمَ مِنْ وَرَاءِ كُلِّ تِجَارَةٍ فَيُعْطَى الْمُلْكَ بِيَمِينِهِ وَالْخُلْدَ بِشِمَالِهِ وَيُوضَعُ عَلَى رَأْسِهِ تَاجُ الْوَقَارِ وَيُكْسَى وَالِدَاهُ حُلَّتَانِ لَا يَقُومُ لَهُمَا أَهْلُ الدُّنْيَا فَيَقُولَانِ بِمَا كُسِينَا هَذَا فَيُقَالُ بِأَخْذِ وَلَدِكُمَا الْقُرْآنَ ثُمَّ يُقَالُ اقْرَأْ وَاصْعَدْ فِي دَرَجِ الْجَنَّةِ وَغُرَفِهَا فَهُوَ فِي صُعُودٍ مَا دَامَ يَقْرأُ هَذًّا كَانَ أَوْ تَرْتِيلً\n" +
                    "\n" +
                    "«В Судный день Коран явится в виде бледного человека и встретит своего чтеца у могилы, когда она откроется. Коран спросит его: «Ты знаешь меня?». И человек ответит: «Нет, я не знаю тебя». И Коран скажет: «Я — твой спутник, Коран, который вызывал у тебя жажду, во время жары, и заставлял подниматься тебя ночью. Каждый купец стоит за своей торговлей, сегодня ты стоишь за всеми видами торговли». А затем в его правую руку будет положена власть, в левую — вечная жизнь, а на голову будет водружена корона достоинства. Его родители будут вознаграждены двумя такими одеждами, которые никто из них не имел в мирской жизни. Они спросят: «Почему нам даны эти одежды?». И им будет отвечено: «Потому что ваш сын изучал Коран». И будет сказано (чтецу Корана): «Взойди, читая, по ступеням Рая и его комнатам». И он будет подниматься столь долго, сколь читает Коран, и не важно, читает он быстро или медленно» (Ахмад, Ибн Маджа).",
            "Передаётся от Абу Саида аль-Худри, что Пророк ﷺ сказал:\n" +
                    "\n" +
                    "يَقُولُ الرَّبُّ عَزَّ وَجَلَّ مَنْ شَغَلَهُ الْقُرْآنُ وَذِكْرِى عَنْ مَسْأَلَتِى أَعْطَيْتُهُ أَفْضَلَ مَا أُعْطِى السَّائِلِينَ وَفَضْلُ كَلَامِ اللَّهِ عَلَى سَائِرِ الْكَلَامِ كَفَضْلِ اللَّهِ عَلَى خَلْقِهِ\n" +
                    "\n" +
                    "«Великий и Всемогущий Господь говорит: «Тому, кого поминание Меня и чтение Корана отвлекло от просьб ко Мне, Я дам лучшее, что даю просящим у Меня». А достоинство речи Аллаха (Корана) в сравнении с достоинством остальной речи подобно достоинству Аллаха над Его творениями» (ат-Тирмизи, который сказал, что хадис хасан гариб).",
            "Передаётся от Абдуллаха ибн Масъуда, что Пророк ﷺ сказал:\n" +
                    "\n" +
                    "اِقْرَءُوا الْقُرْآنَ فَإِنَّ اللَّهَ تَعَالَى لَا يُعَذِّبُ قَلْبًا وَعَى الْقُرْآنَ وَإِنَّ هَذَا الْقُرْآنَ مَأْدُبَةُ اللَّهِ فَمَنْ دَخَلَ فِيهِ فَهُوَ آمِنٌ مَنْ أَحَبَّ الْقُرْآنَ فَلْيُبْشِرْ\n" +
                    "\n" +
                    "«Читайте Коран, ведь Аллах не накажет сердце, наполненное Кораном! Коран — угощение Аллаха, кто пришёл на угощение, тот в безопасности, а кто полюбил Коран, того обрадуйте благой вестью» (ад-Дарими).",
            "Передаётся от Сахля ибн Муаза аль-Джухани, а он — от своего отца: «Посланник Аллаха ﷺ сказал:\n" +
                    "\n" +
                    "مَنْ قَرَأَ الْقُرْآنَ وَعَمِلَ بِمَا فِيهِ أُلْبِسَ وَالِدَاهُ تَاجًا يَوْمَ الْقِيَامَةِ ضَوْءُهُ أَحْسَنُ مِنْ ضَوْءِ الشَّمْسِ فِي بُيُوتِ الدُّنْيَا لَوْ كَانَتْ فِيكُمْ فَمَا ظَنُّكُمْ بِالَّذِي عَمِلَ بِهَذَا\n" +
                    "\n" +
                    "«Отцу и матери человека, который читал Коран и поступал в соответствии с ним, в Судный день наденут корону. Свет этой короны будет прекраснее света солнца, проникающего в дома в земной жизни. Так что вы думаете о положении тех, кто поступал в соответствии с Кораном?!» (Абу Дауд).",
            "Передаётся от Абдуллаха ибн Умара, что Пророк ﷺ сказал:\n" +
                    "\n" +
                    "لَا حَسَدَ إِلَّا فِي اثْنَتَيْنِ رَجُلٌ آتَاهُ اللَّهُ الْقُرْآنَ فَهُوَ يَقُومُ بِهِ آنَاءَ اللَّيلِ وَآنَاءَ النَّهَارِ وَرَجُلٌ آتَاهُ اللَّهُ مَالًا فَهُوَ يُنْفِقُهُ آنَاءَ اللَّيْلِ وَآنَاءَ النَّهَارِ\n" +
                    "\n" +
                    "«Можно завидовать только двум людям: один — это тот, кому Аллах дал знание Корана. Такой человек днём и ночью занят Кораном. Второй — это тот, кому Аллах даровал богатство, и он днём и ночью занят тем, что тратит его (на благом пути)» (Бухари, Муслим).",
            "Передаётся от Абдуллаха ибн Аббаса: «Посланник Аллаха ﷺ сказал:\n" +
                    "\n" +
                    "إِنَّ الَّذِي لَيْسَ فِي جَوْفِهِ شَيْءٌ مِنَ الْقُرْآنِ كَالْبَيْتِ الْخَرِبِ\n" +
                    "\n" +
                    "«Тот, кто не знает ничего из Корана, подобен разрушенному дому» (ат-Тирмизи, который сказал, что хадис хасан сахих).",
            "Сообщается, что Абу Умама, да будет доволен им Аллах, сказал:\n" +
                    "\n" +
                    "— Я слышал, как Посланник Аллаха ﷺ сказал: «Читайте Коран, ибо, поистине, в День воскресения он явится как заступник за тех, кто его читал». (Муслим 804)",
            "Сообщается, что ан-Наввас бин Сам‘ан, да будет доволен им Аллах, сказал:\n" +
                    "\n" +
                    "— Я слышал, как Посланник Аллаха ﷺ сказал: «В День воскресения приведут Коран и тех, кто в мире этом поступал согласно его (установлениям), а впереди него будут идти суры „Корова“ и „Семейство Имрана“, которые станут (выдвигать аргументы в пользу) тех, кто придерживался их». (Муслим 805)",
            " Передают со слов Абу Мусы, да будет доволен им Аллах, что Посланник Аллаха ﷺ сказал:\n" +
                    "\n" +
                    "— Верующий, который читает Коран, подобен сладкому лимону, обладающему приятным запахом и вкусом, а верующий, который не читает Коран, подобен финику, не обладающему запахом, но сладкому на вкус. Лицемер, который читает Коран, подобен базилику, обладающему приятным запахом, но горькому на вкус, а лицемер, который Коран не читает, подобен колоквинту, не обладающему запахом и горькому на вкус. (Аль-Бухари 5427; Муслим 797)",

            "Подружитесь с Кораном, всегда открывайте Коран. Не просто читайте Коран, любите Коран, живите по Корану \n\n© Жители Газзы",
            "Поставьте Аллаха, Коран на первое место и Аллах организует ваши дела \n\n© Жители Газзы",
            "Если у вас нет времени на Коран, на первом ли месте у вас ахира? Как мы воспитываем наших детей, на первом ли месте для них ахира? \n\n© Жители Газзы",
            "Нам не нужна жалость, если хотите нам помочь - оставьте грехи, оставьте грехи, оставьте грехи. Тот кто совершает грехи, его сердце становится черным и не принимает Коран \n\n© Жители Газзы",
            "Начните сегодня как будто это первый день в вашей жизни. Скажите себе: какой грех я сегодня могу оставить чтобы приблизить победу? \n\n© Жители Газзы",
            "Читайте много салаватов, особенно в пятницу, с четверга на пятницу. Когда что-то делаете, спросите себя: если бы сейчас  пришел Пророк \n صلى الله عل\"يه وسلم \n в мой дом и увидел как мы живём ему бы это понравилось?\" - чтобы понять правильно ли мы живём. Перед сном делайте самоотчёт. \n\n© Жители Газзы",
            "Эта дунья проклята, кроме упоминания Аллаха, Корана... \n\n© Жители Газзы",
            "Вы сами выбираете какой будет ваш конец. Если вы подружитесь с Кораном, он вас не оставит при смерти и после смерти \n\n© Жители Газзы",
            "Больше 1300 человек недавно сдали Коран, стали хафизами в Газзе. АльхамдулиЛлях. Как бы мы сейчас читали Коран если бы не знали его наизусть... \n\n© Жители Газзы",
            "Когда ангелы будут допрашивать в могиле, Коран сам будет отвечать за человека \n\n© Жители Газзы",
            "Даже муж/жена, дети, мама, папа - самые любимые люди, никто не будет с вами в могиле, только Коран \n\n© Жители Газзы",
            "Дружите с Кораном, измените себя, чтобы мир изменился к лучшему \n\n© Жители Газзы",
            "Праздная жизнь - оковы дуньи, а Коран - крылья ахырата \n\n© ibn Rustum"
    };

    private void showRandomQuoteOnLaunch() {
        // Генерация случайного индекса
        int randomIndex = new Random().nextInt(quotes.length);

        // Получение случайной цитаты
        String randomQuote = quotes[randomIndex];

        // Показать цитату в диалоге
        showQuoteDialog(randomQuote);
    }

    // Метод для обновления прогресса загрузки
    private void updateProgress(int downloadedFiles, ProgressBar progressBar, TextView progressText) {
        // Обновляем текст прогресса
        String progressString = downloadedFiles + " / 604";
        progressText.setText(progressString);

        // Устанавливаем максимальное значение прогрессбара
        progressBar.setMax(604);

        // Обновляем прогрессбар
        progressBar.setProgress(downloadedFiles);

        // Устанавливаем индикатор выполнения (например, можно показывать процент)
        int progressPercentage = (int) ((downloadedFiles / 604.0) * 100);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(progressPercentage);
    }

    private void playAudio(int pageIndex, DocumentFile directory) {
        /*if (mediaPlayer != null) {
            mediaPlayer.release();
        }*/

        String fileName = String.format("%03d.mp3", pageIndex + 1);
        DocumentFile audioFile = directory.findFile(fileName);

        if (audioFile == null || !audioFile.exists()) {
            Snackbar.make(findViewById(android.R.id.content), "Аудиофайл не найден: " + fileName, Snackbar.LENGTH_LONG).show();
            return;
        }

        //mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioFile.getUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            btnStartPause.setImageResource(R.drawable.pause);

            mediaPlayer.setOnCompletionListener(mp -> {
                // Здесь вы можете определить, что произойдет после завершения воспроизведения файла
                // Например, можно воспроизводить следующий файл или останавливаться
            });
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(android.R.id.content), "Ошибка воспроизведения аудио", Snackbar.LENGTH_LONG).show();
        }

        Snackbar.make(b.getRoot(), "PlayAudio", Snackbar.LENGTH_SHORT).show();
    }



    private void playNextAudioFile() {
        if (currentFileIndex >= audioFiles.size()) {
            currentFileIndex = 0; // Перезапуск с первого файла
        }

        File audioFile = audioFiles.get(currentFileIndex);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            btnStartPause.setImageResource(R.drawable.pause);

            mediaPlayer.setOnCompletionListener(mp -> {
                currentFileIndex++;
                playNextAudioFile();
            });
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(android.R.id.content), "Ошибка воспроизведения аудио", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //TODO
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float x = event.values[0];
                float y = event.values[1];

                if (x > SHAKE_THRESHOLD) {
                    // Наклон направо-вниз, переворачиваем назад
                    turnPageBackward();
                } else if (x < -SHAKE_THRESHOLD) {
                    // Наклон налево-вниз, переворачиваем вперед
                    turnPageForward();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO
    }

    // Интерфейс для обратного вызова завершения загрузки
    public interface DownloadCallback {
        void onDownloadComplete();
        void onDownloadError(String error);
    }



    public void startDownload() {
        DownloadFilesTask downloadFilesTask = new DownloadFilesTask(this);
        List<String> downloadUrls = downloadFilesTask.generateDownloadUrls();
        downloadFilesTask.execute(downloadUrls);
    }


    // Метод для получения количества скачанных файлов
    private int getDownloadedFilesCount() {
        File directory = new File(getFilesDir() + File.separator + "QuranPagesAudio");
        if (!directory.exists()) {
            return 0;
        }

        // Считаем количество файлов в папке
        int count = 0;
        for (int i = 1; i <= 604; i++) {
            String fileName = String.format(Locale.getDefault(), "%03d.mp3", i);
            File file = new File(directory, fileName);
            if (file.exists()) {
                count++;
            }
        }

        return count;
    }

    private void infoGesture() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Инструкция по использованию панели перевода");
        builder.setMessage("Открыть и закрыть панель перевода можно:\n" +
                "1. Двойным кликом по любому месту панели\n" +
                "2. По кнопке \"открыть и закрыть\" справа в углу\n" +
                "3. Свайпами на начале панели:\n" +
                "   - Свайп вверх от начала панели - открыть\n" +
                "   - Свайп вниз от начала панели - закрыть.\n" +
                "   Важно: свайпы работают только на верхней части панели, чтобы не мешать прокрутке перевода (Тафсира)");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Не понял", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), TutorialTafsirPanelActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }


    private void turnPageForward() {
        int nextPage = currentPosition + 1;
        if (nextPage < viewPager.getAdapter().getItemCount()) {
            viewPager.setCurrentItem(nextPage);
        }
    }

    private void turnPageBackward() {
        int previousPage = currentPosition - 1;
        if (previousPage >= 0) {
            viewPager.setCurrentItem(previousPage);
        }
    }

    public interface OnPageChangedListener {
        void onPageChanged(int position);
        int getCurrentPosition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (quranSevenHours != null && quranSevenHours.isPlaying()) {
            quranSevenHours.pause();
            isPlaying = false;
            btnStartPause.setImageResource(R.drawable.play);
            handler.removeCallbacks(updateSeekBarRunnable); // Останавливаем обновление SeekBar
        }

        // Сохраняем прогресс
        int currentPosition = quranSevenHours.getCurrentPosition();
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_SEEK_POSITION, currentPosition);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Пишем в журнал ошибок перед уничтожением активности
        if (quranSevenHours != null) {
            quranSevenHours.release();
            quranSevenHours = null;
        }
        handler.removeCallbacks(updateSeekBarRunnable); // Останавливаем обновление SeekBar

    }

}