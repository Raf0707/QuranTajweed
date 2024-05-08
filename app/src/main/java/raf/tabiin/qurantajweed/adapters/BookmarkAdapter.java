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

    String[] sures = new String[] {
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
            holder.textView.setText("Страница " + page + "\n" + suraTitle);
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