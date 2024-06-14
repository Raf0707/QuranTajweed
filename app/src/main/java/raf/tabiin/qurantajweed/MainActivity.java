package raf.tabiin.qurantajweed;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static java.security.AccessController.getContext;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
import raf.tabiin.qurantajweed.ui.player.players.MusicPlayer;
import raf.tabiin.qurantajweed.utils.OnSwipeTouchListener;
import raf.tabiin.qurantajweed.ui.player.res_downloaders.QuranAudioZipDownloader;

public class MainActivity extends AppCompatActivity implements AsyncHttpClient.DownloadListener {

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
                b.pageNumberText.setText(String.valueOf(position+1));
                b.tafsirText.setText(loadTextFromFile(position+1 + ".txt"));
                b.scrollTafsir.scrollTo(0, 0);
                bookmarkAdapter.notifyDataSetChanged();
                //TODO
                if (isPlaying) {
                    //playAudio(position, );
                }
                //TODO
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

    /*private void goToPageAlert() {

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

    }*/

    private void goToPageAlert() {

        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.go_to_page_dialog, null);

        alert.setTitle("Перейти на страницу");
        alert.setMessage("Введите страницу");
        alert.setCancelable(true);

        EditText pageNum = dialogView.findViewById(R.id.pageNum);
        TextView suraTitle = dialogView.findViewById(R.id.suraTitleAyats);
        AutoCompleteTextView suraClassic = dialogView.findViewById(R.id.suraClassic);
        AutoCompleteTextView ayatClassic = dialogView.findViewById(R.id.ayatClassic);

        // Добавляем TextWatcher для обновления названия суры и аятов при вводе номера страницы
        pageNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не требуется реализация
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Не требуется реализация
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
                        suraTitle.setText(bookmarkAdapter.getSuraTitle(page)+",\n"+bookmarkAdapter.getAyatsOnPage(page));

                    } else {
                        suraTitle.setText("");
                    }
                } else {
                    suraTitle.setText("");
                }
            }
        });

        // Добавляем TextWatcher для обновления страницы при вводе суры и аята
        TextWatcher suraAyatWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не требуется реализация
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Не требуется реализация
            }

            @Override
            public void afterTextChanged(Editable s) {
                String suraInput = suraClassic.getText().toString().replaceAll("[\\.\\-,\\s]+", "");
                String ayatInput = ayatClassic.getText().toString().replaceAll("[\\.\\-,\\s]+", "");

                if (!suraInput.isEmpty() && !ayatInput.isEmpty()) {
                    int sura, ayat;
                    try {
                        sura = Integer.parseInt(suraInput);
                        ayat = Integer.parseInt(ayatInput);
                    } catch (NumberFormatException e) {
                        pageNum.setText("");
                        return;
                    }
                    int page = bookmarkAdapter.goToAyat(sura, ayat);
                    if (page != -1) {
                        pageNum.setText(String.valueOf(page));
                        suraTitle.setText(bookmarkAdapter.getSuraTitle(page) + ",\n" + bookmarkAdapter.getAyatsOnPage(page));
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