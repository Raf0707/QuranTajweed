package raf.tabiin.qurantajweed.adapters;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.model.Bookmark;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private static final String BOOKMARKS_FILE_NAME = "bookmarks.json";

    private int[] numPageSures = new int[]{
            0, 1, 49, 76, 105, 127, 150, 176, 186, 207, 220, 234, 248, 254, 261, 266, 281, 292, 304, 311, 321, 331, 341, 349, 358, 366, 376, 384, 395, 403, 410, 414, 417, 427, 433, 439, 445, 452, 457, 466, 476, 482, 488, 495, 498, 501, 506, 510, 514, 517, 519, 522, 525, 527, 530, 533, 536, 541, 544, 548, 550, 552, 553, 555, 657, 559, 561, 563, 565, 567, 569, 571, 573, 574, 576, 577, 579, 581, 582, 584, 585, 586, 586, 588, 589, 590, 590, 591, 592, 593, 594, 594, 595, 595, 596, 596, 597, 597, 598, 598, 599, 599, 600, 600, 600, 601, 601, 601, 602, 602, 602, 603, 603, 603, 604, 604, 604
    };      

    String[] sures = {
            "1. «Аль-Фатиха» \n («Открывающая Книгу»)",
            "2. «Аль-Бакара» \n («Корова»)",
            "3. «Аль-Имран» \n («Семейство Имрана»)",
            "4. «Ан-Ниса» \n («Женщины»)",
            "5. «Аль-Маида» \n («Трапеза»)",
            "6. «Аль-Анам» \n («Скот»)",
            "7. «Аль-Араф» \n («Преграды»)",
            "8. «Аль-Анфаль» \n («Трофеи»)",
            "9. «Ат-Тауба» \n («Покаяние»)",
            "10. «Йунус» \n («Иона»)",
            "11. «Худ» \n («Худ»)",
            "12. «Юсуф» \n («Юсуф»)",
            "13. «Ар-Ра’д» \n («Гром»)",
            "14. «Ибрахим» \n («Абрахам»)",
            "15. «Аль-Хиджр» \n («Хиджр»)",
            "16. «Ан-Наль» \n («Пчелы»)",
            "17. «Аль-Исра» \n («Ночной перенос»)",
            "18. «Аль-Кахф» \n («Пещера»)",
            "19. «Марьям» \n («Мария»)",
            "20. «Таха» \n («Та Ха»)",
            "21. «Аль-Анбиа» \n («Пророки»)",
            "22. «Аль-Хадж» \n («Паломничество»)",
            "23. «Аль-Муминун» \n («Верующие»)",
            "24. «Ан-Нур» \n («Свет»)",
            "25. «Аль-Фуркан» \n («Различение»)",
            "26. «Аш-Шуара» \n («Поэты»)",
            "27. «Ан-Намл» \n («Муравьи»)",
            "28. «Аль-Касас» \n («Рассказ»)",
            "29. «Аль-Анкабут» \n («Паук»)",
            "30. «Ар-Рум» \n («Римляне»)",
            "31. «Лукман» \n («Лукман»)",
            "32. «Ас-Саджда» \n («Поклонение»)",
            "33. «Аль-Ахзаб» \n («Союзники»)",
            "34. «Саба» \n («Сава»)",
            "35. «Фатыр» \n («Творец»)",
            "36. «Ясин» \n («Ясин»)",
            "37. «Ас-Саффат» \n («Стоящие рядами»)",
            "38. «Сад» \n («Сад»)",
            "39. «Аз-Зумар» \n («Толпы»)",
            "40. «Гафир» \n («Прощающий»)",
            "41. «Фуссилат» \n («Разъяснены»)",
            "42. «Аш-Шура» \n («Совет»)",
            "43. «Аз-Зухруф» \n («Украшение»)",
            "44. «Ад-Духан» \n («Дым»)",
            "45. «Аль-Джазия» \n («Коленопреклоненные»)",
            "46. «Аль-Ахкаф» \n («Бархианы»)",
            "47. «Мухаммад» \n («Мухаммад»)",
            "48. «Аль-Фатх» \n («Победа»)",
            "49. «Аль-Худжурат» \n («Комнаты»)",
            "50. «Каф» \n («Каф»)",
            "51. «Аз-Зарият» \n («Рассеивающие»)",
            "52. «Ат-Тур» \n («Гора»)",
            "53. «Ан-Наджм» \n («Звезда»)",
            "54. «Аль-Камар» \n («Луна»)",
            "55. «Ар-Рахман» \n («Милостивый»)",
            "56. «Аль-Вакиа» \n («Неизбежное»)",
            "57. «Аль-Хадид» \n («Железо»)",
            "58. «Аль-Муджадила» \n («Онемевшая»)",
            "59. «Аль-Хашр» \n («Сбор»)",
            "60. «Аль-Мумтагана» \n («Испытуемые»)",
            "61. «Ас-Сафф» \n («Ряд»)",
            "62. «Аль-Джумуа» \n («Пятница»)",
            "63. «Аль-Мунафикун» \n («Лицемеры»)",
            "64. «Ат-Тагабун» \n («Обделение»)",
            "65. «Ат-Таляк» \n («Развод»)",
            "66. «Ат-Тахрим» \n («Запрещение»)",
            "67. «Аль-Мульк» \n («Царство»)",
            "68. «Аль-Калам» \n («Перо»)",
            "69. «Аль-Хакка» \n («Неизбежное»)",
            "70. «Аль-Маарид» \n («Ступени»)",
            "71. «Нух» \n («Ной»)",
            "72. «Аль-Джинн» \n («Джинны»)",
            "73. «Аль-Муззаммиль» \n («Завернутый»)",
            "74. «Аль-Муддассир» \n («Завернувшийся в одежду»)",
            "75. «Аль-Кийама» \n («Воскрешение»)",
            "76. «Ад-Дахар» \n («Человек»)",
            "77. «Аль-Мурсалат» \n («Посланные»)",
            "78. «Ан-Наба» \n («Весть»)",
            "79. «Ан-Назиат» \n («Вырывающие»)",
            "80. «Абаса» \n («Он нахмурился»)",
            "81. «Ат-Таквир» \n («Сворачивание»)",
            "82. «Аль-Инфитар» \n («Разрушение»)",
            "83. «Аль-Мутаффифин» \n («Обвешивающие»)",
            "84. «Аль-Иншикак» \n («Разверзнется»)",
            "85. «Аль-Бурудж» \n («Созвездия»)",
            "86. «Ат-Тарик» \n («Ночной гость»)",
            "87. «Аль-Аля» \n («Всевышний»)",
            "88. «Аль-Гашия» \n («Покрывающее»)",
            "89. «Аль-Фаджр» \n («Заря»)",
            "90. «Аль-Балад» \n («Город»)",
            "91. «Аш-Шамс» \n («Солнце»)",
            "92. «Аль-Лайл» \n («Ночь»)",
            "93. «Ад-Духа» \n («Утро»)",
            "94. «Аль-Инширох» \n («Раскрытие»)",
            "95. «Ат-Тин» \n («Смоковница»)",
            "96. «Аль-Аляк» \n («Сгусток»)",
            "97. «Аль-Кадр» \n («Ночь силы»)",
            "98. «Аль-Баййина» \n («Ясное доказательство»)",
            "99. «Аз-Залзала» \n («Сотрясение»)",
            "100. «Аль-Адият» \n («Наездницы»)",
            "101. «Аль-Кариа» \n («Катастрофа»)",
            "102. «Ат-Такатур» \n («Стремление к богатству»)",
            "103. «Аль-Аср» \n («Время»)",
            "104. «Аль-Хумаза» \n («Клеветники»)",
            "105. «Аль-Филь» \n («Слон»)",
            "106. «Курайш» \n («Курайшиты»)",
            "107. «Аль-Маун» \n («Милостыня»)",
            "108. «Аль-Каусар» \n («Изобилие»)",
            "109. «Аль-Кафирун» \n («Неверующие»)",
            "110. «Ан-Наср» \n («Помощь»)",
            "111. «Аль-Лахаб» \n («Пламя»)",
            "112. «Аль-Ихлас» \n («Единство»)",
            "113. «Аль-Фалак» \n («Заря»)",
            "114. «Ан-Нас» \n («Люди»)"
    };


    private Context context;
    private List<Bookmark> bookmarks;
    private SharedPreferences preferences;
    private Gson gson;
    private int currentPosition;
    private ViewPager2 quranPager;

    public BookmarkAdapter(Context context, ViewPager2 quranPager) {
        this.context = context;
        this.gson = new Gson();
        this.bookmarks = loadBookmarks();
        this.quranPager = quranPager;
    }

    public List<Bookmark> loadBookmarks() {
        List<Bookmark> loadedBookmarks = new ArrayList<>();
        try {
            // Откройте файл bookmarks.json из папки assets/Bookmarks
            InputStream inputStream = context.getAssets().open("Bookmarks/bookmarks.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type type = new TypeToken<List<Bookmark>>() {}.getType();
            loadedBookmarks = gson.fromJson(reader, type);
            reader.close();
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return loadedBookmarks != null ? loadedBookmarks : new ArrayList<>();
    }

    // Метод для сохранения закладок в JSON-файл
    private void saveBookmarks() {
        File bookmarksFile = new File(context.getFilesDir(), BOOKMARKS_FILE_NAME);
        try (FileWriter writer = new FileWriter(bookmarksFile)) {
            String json = gson.toJson(bookmarks);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    // Методы для управления закладками

    public void addBookmark(Bookmark bookmark) {
        if (!isBookmarked(currentPosition)) {
            bookmarks.add(bookmark);
            saveBookmarks();
            notifyDataSetChanged();
        }
    }

    public void removeBookmark(int position) {
        bookmarks.removeIf(bookmark -> bookmark.getPosition() == position);
        saveBookmarks();
        notifyDataSetChanged();
    }


    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);

        // Найти индекс суры по номеру страницы закладки
        int page = bookmark.getPosition() + 1; // Добавляем 1, так как страницы начинаются с 1, а не 0
        int suraIndex = getSuraIndex(page);

        // Если индекс суры найден, добавляем название суры к тексту
        if (suraIndex != -1) {
            String suraTitle = sures[suraIndex - 1];
            holder.textView.setText("Страница " + page + "\nСура " + suraTitle);
        } else {
            // Если сура не найдена (что маловероятно), установим просто номер страницы
            holder.textView.setText("Страница " + page);
        }

        // Устанавливаем слушателя нажатий
        holder.itemView.setOnClickListener(view -> {
            // Вызывайте метод, чтобы переместить ViewPager2 на страницу, связанную с закладкой
            navigateToPage(bookmark.getPosition());
        });
    }


    @Override
    public int getItemCount() {
        // Добавлена проверка null перед использованием bookmarks
        return (bookmarks != null) ? bookmarks.size() : 0;
    }

    private void navigateToPage(int page) {
        // Предполагаем, что у вас есть ссылка на ViewPager2
        quranPager.setCurrentItem(page);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Здесь вы можете объявить элементы интерфейса для отображения данных закладки
        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Инициализация элементов интерфейса
            textView = itemView.findViewById(R.id.bookmarkText);
        }
    }

    public boolean isBookmarked(int position) {
        // Пройдите по списку закладок и проверьте, совпадает ли позиция закладки с заданной
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getPosition() == position) {
                return true;
            }
        }
        return false;
    }

    private int getSuraIndex(int page) {
        for (int i = 0; i < numPageSures.length - 1; i++) {
            if (page >= numPageSures[i] && page < numPageSures[i + 1]) {
                return i;
            }
        }
        // Если страница равна или выше последнего начального номера страницы суры
        if (page >= numPageSures[numPageSures.length - 1]) {
            return numPageSures.length - 1;
        }
        // Возврат -1, если страница не попала ни в один диапазон (такого не должно случиться)
        return -1;
    }

    /*
     * Задачи:
     * 1. Реализовать перемещение к закладке (при нажатии на закладку перемещаться во ViewPager2)
     * к заданной странице
     * 2. Исправить ошибку с закладками (для каждой позиции должна быть отдельная закладка)
     */

}