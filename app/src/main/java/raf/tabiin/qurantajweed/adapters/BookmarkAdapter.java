package raf.tabiin.qurantajweed.adapters;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import raf.tabiin.qurantajweed.R;
import raf.tabiin.qurantajweed.model.Bookmark;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private static final String BOOKMARKS_FILE_NAME = "bookmarks.json";

    private int[] numPageSures = new int[]{
            0, 1, 49, 76, 105, 127, 150, 176, 186, 207, 220, 234, 248, 254, 261, 266, 281, 292, 304, 311, 321, 331, 341, 349, 358, 366, 376, 384, 395, 403, 410, 414, 417, 427, 433, 439, 445, 452, 457, 466, 476, 482, 488, 495, 498, 501, 506, 510, 514, 517, 519, 522, 525, 527, 530, 533, 536, 541, 544, 548, 550, 552, 553, 555, 557, 559, 561, 563, 565, 567, 569, 571, 573, 574, 576, 577, 579, 581, 582, 584, 585, 586, 586, 588, 589, 590, 590, 591, 592, 593, 594, 594, 595, 595, 596, 596, 597, 597, 598, 598, 599, 599, 600, 600, 600, 601, 601, 601, 602, 602, 602, 603, 603, 603
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
            "Сура 63. Аль-Мунафикун — Лицемеры",
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

    Map<String, Integer> suraMap = createSuraMap(sures, numPageSures);


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
            // Получаем путь к внутренней директории приложения
            File file = new File(context.getFilesDir(), "bookmarks.json");
            // Открываем FileInputStream для чтения файла
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream);
            // Считываем JSON данные и преобразуем их в список закладок
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
        holder.textView.setText("Страница " + page + "\n" + getSuraTitle(page) + "\n" + getAyatsOnPage(page));


        // Устанавливаем слушателя нажатий
        holder.itemView.setOnClickListener(view -> {
            // Вызывайте метод, чтобы переместить ViewPager2 на страницу, связанную с закладкой
            navigateToPage(bookmark.getPosition());
        });

        holder.deleteBookmark.setOnClickListener(v -> {
            //bookmarks.remove(bookmarks.get(position));
            removeBookmark(bookmark.getPosition());
            saveBookmarks();
            notifyDataSetChanged();
            //Log.d("Remove Bookmark", ""+position);
        });
    }

    private String getAyatsOnPage(int page) {
        switch (page) {
            //Сура Аль-Фатиха
            case 1: return " аяты: 1-7";

            //Сура Аль-Бакара
            case 2: return " аяты: 1-5";
            case 3: return " аяты: 6-16";
            case 4: return " аяты: 17-24";
            case 5: return " аяты: 25-29";
            case 6: return " аяты: 30-37";
            case 7: return " аяты: 38-48";
            case 8: return " аяты: 49-57";
            case 9: return " аяты: 58-61";
            case 10: return " аяты: 62-69";
            case 11: return " аяты: 70-76";
            case 12: return " аяты: 77-83";
            case 13: return " аяты: 84-88";
            case 14: return " аяты: 89-93";
            case 15: return " аяты: 94-101";
            case 16: return " аяты: 102-105";
            case 17: return " аяты: 106-112";
            case 18: return " аяты: 113-119";
            case 19: return " аяты: 120-126";
            case 20: return " аяты: 127-134";
            case 21: return " аяты: 135-141";
            case 22: return " аяты: 142-145";
            case 23: return " аяты: 146-153";
            case 24: return " аяты: 154-163";
            case 25: return " аяты: 164-169";
            case 26: return " аяты: 170-176";
            case 27: return " аяты: 177-181";
            case 28: return " аяты: 182-186";
            case 29: return " аяты: 187-190";
            case 30: return " аяты: 191-196";
            case 31: return " аяты: 197-202";
            case 32: return " аяты: 203-210";
            case 33: return " аяты: 211-215";
            case 34: return " аяты: 216-219";
            case 35: return " аяты: 220-224";
            case 36: return " аяты: 225-230";
            case 37: return " аяты: 231-233";
            case 38: return " аяты: 234-237";
            case 39: return " аяты: 238-245";
            case 40: return " аяты: 246-248";
            case 41: return " аяты: 249-252";
            case 42: return " аяты: 253-256";
            case 43: return " аяты: 257-259";
            case 44: return " аяты: 260-264";
            case 45: return " аяты: 265-269";
            case 46: return " аяты: 270-274";
            case 47: return " аяты: 275-281";
            case 48: return " аят: 282";
            case 49: return " аяты: 283-286";

            //Сура Аль-Имран
            case 50: return " аяты: 1-9";
            case 51: return " аяты: 10-15";
            case 52: return " аяты: 16-22";
            case 53: return " аяты: 23-29";
            case 54: return " аяты: 30-37";
            case 55: return " аяты: 38-45";
            case 56: return " аяты: 46-52";
            case 57: return " аяты: 53-61";
            case 58: return " аяты: 62-70";
            case 59: return " аяты: 71-77";
            case 60: return " аяты: 78-83";
            case 61: return " аяты: 84-91";
            case 62: return " аяты: 92-100";
            case 63: return " аяты: 101-108";
            case 64: return " аяты: 109-115";
            case 65: return " аяты: 116-121";
            case 66: return " аяты: 122-132";
            case 67: return " аяты: 133-140";
            case 68: return " аяты: 141-148";
            case 69: return " аяты: 149-153";
            case 70: return " аяты: 154-157";
            case 71: return " аяты: 158-165";
            case 72: return " аяты: 166-173";
            case 73: return " аяты: 174-180";
            case 74: return " аяты: 181-186";
            case 75: return " аяты: 187-194";
            case 76: return " аяты: 195-200";

            //Сура Ан-Ниса
            case 77: return " аяты: 1-6";
            case 78: return " аяты: 7-11";
            case 79: return " аяты: 12-14";
            case 80: return " аяты: 15-19";
            case 81: return " аяты: 20-23";
            case 82: return " аяты: 24-26";
            case 83: return " аяты: 27-33";
            case 84: return " аяты: 34-37";
            case 85: return " аяты: 38-44";
            case 86: return " аяты: 45-51";
            case 87: return " аяты: 52-59";
            case 88: return " аяты: 60-65";
            case 89: return " аяты: 66-74";
            case 90: return " аяты: 75-79";
            case 91: return " аяты: 80-86";
            case 92: return " аяты: 87-91";
            case 93: return " аяты: 92-94";
            case 94: return " аяты: 95-101";
            case 95: return " аяты: 102-105";
            case 96: return " аяты: 106-113";
            case 97: return " аяты: 114-121";
            case 98: return " аяты: 122-127";
            case 99: return " аяты: 128-134";
            case 100: return " аяты: 135-140";
            case 101: return " аяты: 141-147";
            case 102: return " аяты: 148-153";
            case 103: return " аяты: 154-162";
            case 104: return " аяты: 163-170";
            case 105: return " аяты: 171-175";

            //Конец суры Ан-Ниса, начало Суры Аль-Маида
            case 106: return "";
            case 107: return " аяты: 3-5";
            case 108: return " аяты: 6-9";
            case 109: return " аяты: 10-13";
            case 110: return " аяты: 14-17";
            case 111: return " аяты: 18-23";
            case 112: return " аяты: 24-311";
            case 113: return " аяты: 32-36";
            case 114: return " аяты: 37-41";
            case 115: return " аяты: 42-45";
            case 116: return " аяты: 46-50";
            case 117: return " аяты: 51-57";
            case 118: return " аяты: 58-64";
            case 119: return " аяты: 65-70";
            case 120: return " аяты: 71-76";
            case 121: return " аяты: 77-82";
            case 122: return " аяты: 83-89";
            case 123: return " аяты: 90-95";
            case 124: return " аяты: 96-103";
            case 125: return " аяты: 104-108";
            case 126: return " аяты: 109-113";
            case 127: return " аяты: 114-120";

            //Сура Аль-Ан'ам
            case 128: return " аяты: 1-8";
            case 129: return " аяты: 9-18";
            case 130: return " аяты: 19-27";
            case 131: return " аяты: 28-35";
            case 132: return " аяты: 36-44";
            case 133: return " аяты: 45-52";
            case 134: return " аяты: 53-59";
            case 135: return " аяты: 60-68";
            case 136: return " аяты: 69-73";
            case 137: return " аяты: 74-81";
            case 138: return " аяты: 82-90";
            case 139: return " аяты: 91-94";
            case 140: return " аяты: 95-101";
            case 141: return " аяты: 102-110";
            case 142: return " аяты: 111-118";
            case 143: return " аяты: 119-124";
            case 144: return " аяты: 125-131";
            case 145: return " аяты: 132-137";
            case 146: return " аяты: 138-142";
            case 147: return " аяты: 143-146";
            case 148: return " аяты: 147-151";
            case 149: return " аяты: 152-157";
            case 150: return " аяты: 158-165";

            //Сура Аль-А'раф
            case 151: return " аяты: 1-11";
            case 152: return " аяты: 12-22";
            case 153: return " аяты: 23-30";
            case 154: return " аяты: 31-37";
            case 155: return " аяты: 38-43";
            case 156: return " аяты: 44-51";
            case 157: return " аяты: 52-57";
            case 158: return " аяты: 58-67";
            case 159: return " аяты: 68-73";
            case 160: return " аяты: 74-81";
            case 161: return " аяты: 82-87";
            case 162: return " аяты: 88-95";
            case 163: return " аяты: 96-104";
            case 164: return " аяты: 105-120";
            case 165: return " аяты: 121-130";
            case 166: return " аяты: 131-137";
            case 167: return " аяты: 138-143";
            case 168: return " аяты: 144-149";
            case 169: return " аяты: 150-155";
            case 170: return " аяты: 156-159";
            case 171: return " аяты: ";
            case 172: return " аяты: ";
            case 173: return " аяты: ";
            case 174: return " аяты: ";
            case 175: return " аяты: ";
            case 176: return " аяты: ";
            case 177: return " аяты: ";
            case 178: return " аяты: ";
            case 179: return " аяты: ";
            case 180: return " аяты: ";
            case 181: return " аяты: ";
            case 182: return " аяты: ";
            case 183: return " аяты: ";
            case 184: return " аяты: ";
            case 185: return " аяты: ";
            case 186: return " аяты: ";
            case 187: return " аяты: ";
            case 188: return " аяты: ";
            case 189: return " аяты: ";
            case 190: return " аяты: ";
            case 191: return " аяты: ";
            case 192: return " аяты: ";
            case 193: return " аяты: ";
            case 194: return " аяты: ";
            case 195: return " аяты: ";
            case 196: return " аяты: ";
            case 197: return " аяты: ";
            case 198: return " аяты: ";
            case 199: return " аяты: ";
            case 200: return " аяты: ";
            case 201: return " аяты: ";
            case 202: return " аяты: ";
            case 203: return " аяты: ";
            case 204: return " аяты: ";
            case 205: return " аяты: ";
            case 206: return " аяты: ";
            case 207: return " аяты: ";
            case 208: return " аяты: ";
            case 209: return " аяты: ";
            case 210: return " аяты: ";
            case 211: return " аяты: ";
            case 212: return " аяты: ";
            case 213: return " аяты: ";
            case 214: return " аяты: ";
            case 215: return " аяты: ";
            case 216: return " аяты: ";
            case 217: return " аяты: ";
            case 218: return " аяты: ";
            case 219: return " аяты: ";
            case 220: return " аяты: ";
            case 221: return " аяты: ";
            case 222: return " аяты: ";
            case 223: return " аяты: ";
            case 224: return " аяты: ";
            case 225: return " аяты: ";
            case 226: return " аяты: ";
            case 227: return " аяты: ";
            case 228: return " аяты: ";
            case 229: return " аяты: ";
            case 230: return " аяты: ";
            case 231: return " аяты: ";
            case 232: return " аяты: ";
            case 233: return " аяты: ";
            case 234: return " аяты: ";
            case 235: return " аяты: ";
            case 236: return " аяты: ";
            case 237: return " аяты: ";
            case 238: return " аяты: ";
            case 239: return " аяты: ";
            case 240: return " аяты: ";
            case 241: return " аяты: ";
            case 242: return " аяты: ";
            case 243: return " аяты: ";
            case 244: return " аяты: ";
            case 245: return " аяты: ";
            case 246: return " аяты: ";
            case 247: return " аяты: ";
            case 248: return " аяты: ";
            case 249: return " аяты: ";
            case 250: return " аяты: ";
            case 251: return " аяты: ";
            case 252: return " аяты: ";
            case 253: return " аяты: ";
            case 254: return " аяты: ";
            case 255: return " аяты: ";
            case 256: return " аяты: ";
            case 257: return " аяты: ";
            case 258: return " аяты: ";
            case 259: return " аяты: ";
            case 260: return " аяты: ";
            case 261: return " аяты: ";
            case 262: return " аяты: ";
            case 263: return " аяты: ";
            case 264: return " аяты: ";
            case 265: return " аяты: ";
            case 266: return " аяты: ";
            case 267: return " аяты: ";
            case 268: return " аяты: ";
            case 269: return " аяты: ";
            case 270: return " аяты: ";
            case 271: return " аяты: ";
            case 272: return " аяты: ";
            case 273: return " аяты: ";
            case 274: return " аяты: ";
            case 275: return " аяты: ";
            case 276: return " аяты: ";
            case 277: return " аяты: ";
            case 278: return " аяты: ";
            case 279: return " аяты: ";
            case 280: return " аяты: ";
            case 281: return " аяты: ";
            case 282: return " аяты: ";
            case 283: return " аяты: ";
            case 284: return " аяты: ";
            case 285: return " аяты: ";
            case 286: return " аяты: ";
            case 287: return " аяты: ";
            case 288: return " аяты: ";
            case 289: return " аяты: ";
            case 290: return " аяты: ";
            case 291: return " аяты: ";
            case 292: return " аяты: ";
            case 293: return " аяты: ";
            case 294: return " аяты: ";
            case 295: return " аяты: ";
            case 296: return " аяты: ";
            case 297: return " аяты: ";
            case 298: return " аяты: ";
            case 299: return " аяты: ";
            case 300: return " аяты: ";
            case 301: return " аяты: ";
            case 302: return " аяты: ";
            case 303: return " аяты: ";
            case 304: return " аяты: ";
            case 305: return " аяты: ";
            case 306: return " аяты: ";
            case 307: return " аяты: ";
            case 308: return " аяты: ";
            case 309: return " аяты: ";
            case 310: return " аяты: ";
            case 311: return " аяты: ";
            case 312: return " аяты: ";
            case 313: return " аяты: ";
            case 314: return " аяты: ";
            case 315: return " аяты: ";
            case 316: return " аяты: ";
            case 317: return " аяты: ";
            case 318: return " аяты: ";
            case 319: return " аяты: ";
            case 320: return " аяты: ";
            case 321: return " аяты: ";
            case 322: return " аяты: ";
            case 323: return " аяты: ";
            case 324: return " аяты: ";
            case 325: return " аяты: ";
            case 326: return " аяты: ";
            case 327: return " аяты: ";
            case 328: return " аяты: ";
            case 329: return " аяты: ";
            case 330: return " аяты: ";
            case 331: return " аяты: ";
            case 332: return " аяты: ";
            case 333: return " аяты: ";
            case 334: return " аяты: ";
            case 335: return " аяты: ";
            case 336: return " аяты: ";
            case 337: return " аяты: ";
            case 338: return " аяты: ";
            case 339: return " аяты: ";
            case 340: return " аяты: ";
            case 341: return " аяты: ";
            case 342: return " аяты: ";
            case 343: return " аяты: ";
            case 344: return " аяты: ";
            case 345: return " аяты: ";
            case 346: return " аяты: ";
            case 347: return " аяты: ";
            case 348: return " аяты: ";
            case 349: return " аяты: ";
            case 350: return " аяты: ";
            case 351: return " аяты: ";
            case 352: return " аяты: ";
            case 353: return " аяты: ";
            case 354: return " аяты: ";
            case 355: return " аяты: ";
            case 356: return " аяты: ";
            case 357: return " аяты: ";
            case 358: return " аяты: ";
            case 359: return " аяты: ";
            case 360: return " аяты: ";
            case 361: return " аяты: ";
            case 362: return " аяты: ";
            case 363: return " аяты: ";
            case 364: return " аяты: ";
            case 365: return " аяты: ";
            case 366: return " аяты: ";
            case 367: return " аяты: ";
            case 368: return " аяты: ";
            case 369: return " аяты: ";
            case 370: return " аяты: ";
            case 371: return " аяты: ";
            case 372: return " аяты: ";
            case 373: return " аяты: ";
            case 374: return " аяты: ";
            case 375: return " аяты: ";
            case 376: return " аяты: ";
            case 377: return " аяты: ";
            case 378: return " аяты: ";
            case 379: return " аяты: ";
            case 380: return " аяты: ";
            case 381: return " аяты: ";
            case 382: return " аяты: ";
            case 383: return " аяты: ";
            case 384: return " аяты: ";
            case 385: return " аяты: ";
            case 386: return " аяты: ";
            case 387: return " аяты: ";
            case 388: return " аяты: ";
            case 389: return " аяты: ";
            case 390: return " аяты: ";
            case 391: return " аяты: ";
            case 392: return " аяты: ";
            case 393: return " аяты: ";
            case 394: return " аяты: ";
            case 395: return " аяты: ";
            case 396: return " аяты: ";
            case 397: return " аяты: ";
            case 398: return " аяты: ";
            case 399: return " аяты: ";
            case 400: return " аяты: ";
            case 401: return " аяты: ";
            case 402: return " аяты: ";
            case 403: return " аяты: ";
            case 404: return " аяты: ";
            case 405: return " аяты: ";
            case 406: return " аяты: ";
            case 407: return " аяты: ";
            case 408: return " аяты: ";
            case 409: return " аяты: ";
            case 410: return " аяты: ";
            case 411: return " аяты: ";
            case 412: return " аяты: ";
            case 413: return " аяты: ";
            case 414: return " аяты: ";
            case 415: return " аяты: ";
            case 416: return " аяты: ";
            case 417: return " аяты: ";
            case 418: return " аяты: ";
            case 419: return " аяты: ";
            case 420: return " аяты: ";
            case 421: return " аяты: ";
            case 422: return " аяты: ";
            case 423: return " аяты: ";
            case 424: return " аяты: ";
            case 425: return " аяты: ";
            case 426: return " аяты: ";
            case 427: return " аяты: ";
            case 428: return " аяты: ";
            case 429: return " аяты: ";
            case 430: return " аяты: ";
            case 431: return " аяты: ";
            case 432: return " аяты: ";
            case 433: return " аяты: ";
            case 434: return " аяты: ";
            case 435: return " аяты: ";
            case 436: return " аяты: ";
            case 437: return " аяты: ";
            case 438: return " аяты: ";
            case 439: return " аяты: ";
            case 440: return " аяты: ";
            case 441: return " аяты: ";
            case 442: return " аяты: ";
            case 443: return " аяты: ";
            case 444: return " аяты: ";
            case 445: return " аяты: ";
            case 446: return " аяты: ";
            case 447: return " аяты: ";
            case 448: return " аяты: ";
            case 449: return " аяты: ";
            case 450: return " аяты: ";
            case 451: return " аяты: ";
            case 452: return " аяты: ";
            case 453: return " аяты: ";
            case 454: return " аяты: ";
            case 455: return " аяты: ";
            case 456: return " аяты: ";
            case 457: return " аяты: ";
            case 458: return " аяты: ";
            case 459: return " аяты: ";
            case 460: return " аяты: ";
            case 461: return " аяты: ";
            case 462: return " аяты: ";
            case 463: return " аяты: ";
            case 464: return " аяты: ";
            case 465: return " аяты: ";
            case 466: return " аяты: ";
            case 467: return " аяты: ";
            case 468: return " аяты: ";
            case 469: return " аяты: ";
            case 470: return " аяты: ";
            case 471: return " аяты: ";
            case 472: return " аяты: ";
            case 473: return " аяты: ";
            case 474: return " аяты: ";
            case 475: return " аяты: ";
            case 476: return " аяты: ";
            case 477: return " аяты: ";
            case 478: return " аяты: ";
            case 479: return " аяты: ";
            case 480: return " аяты: ";
            case 481: return " аяты: ";
            case 482: return " аяты: ";
            case 483: return " аяты: ";
            case 484: return " аяты: ";
            case 485: return " аяты: ";
            case 486: return " аяты: ";
            case 487: return " аяты: ";
            case 488: return " аяты: ";
            case 489: return " аяты: ";
            case 490: return " аяты: ";
            case 491: return " аяты: ";
            case 492: return " аяты: ";
            case 493: return " аяты: ";
            case 494: return " аяты: ";
            case 495: return " аяты: ";
            case 496: return " аяты: ";
            case 497: return " аяты: ";
            case 498: return " аяты: ";
            case 499: return " аяты: ";
            case 500: return " аяты: ";
            case 501: return " аяты: ";
            case 502: return " аяты: ";
            case 503: return " аяты: ";
            case 504: return " аяты: ";
            case 505: return " аяты: ";
            case 506: return " аяты: ";
            case 507: return " аяты: ";
            case 508: return " аяты: ";
            case 509: return " аяты: ";
            case 510: return " аяты: ";
            case 511: return " аяты: ";
            case 512: return " аяты: ";
            case 513: return " аяты: ";
            case 514: return " аяты: ";
            case 515: return " аяты: ";
            case 516: return " аяты: ";
            case 517: return " аяты: ";
            case 518: return " аяты: ";
            case 519: return " аяты: ";
            case 520: return " аяты: ";
            case 521: return " аяты: ";
            case 522: return " аяты: ";
            case 523: return " аяты: ";
            case 524: return " аяты: ";
            case 525: return " аяты: ";
            case 526: return " аяты: ";
            case 527: return " аяты: ";
            case 528: return " аяты: ";
            case 529: return " аяты: ";
            case 530: return " аяты: ";
            case 531: return " аяты: ";
            case 532: return " аяты: ";
            case 533: return " аяты: ";
            case 534: return " аяты: ";
            case 535: return " аяты: ";
            case 536: return " аяты: ";
            case 537: return " аяты: ";
            case 538: return " аяты: ";
            case 539: return " аяты: ";
            case 540: return " аяты: ";
            case 541: return " аяты: ";
            case 542: return " аяты: ";
            case 543: return " аяты: ";
            case 544: return " аяты: ";
            case 545: return " аяты: ";
            case 546: return " аяты: ";
            case 547: return " аяты: ";
            case 548: return " аяты: ";
            case 549: return " аяты: ";
            case 550: return " аяты: ";
            case 551: return " аяты: ";
            case 552: return " аяты: ";
            case 553: return " аяты: ";
            case 554: return " аяты: ";
            case 555: return " аяты: ";
            case 556: return " аяты: ";
            case 557: return " аяты: ";
            case 558: return " аяты: ";
            case 559: return " аяты: ";
            case 560: return " аяты: ";
            case 561: return " аяты: ";
            case 562: return " аяты: ";
            case 563: return " аяты: ";
            case 564: return " аяты: ";
            case 565: return " аяты: ";
            case 566: return " аяты: ";
            case 567: return " аяты: ";
            case 568: return " аяты: ";
            case 569: return " аяты: ";
            case 570: return " аяты: ";
            case 571: return " аяты: ";
            case 572: return " аяты: ";
            case 573: return " аяты: ";
            case 574: return " аяты: ";
            case 575: return " аяты: ";
            case 576: return " аяты: ";
            case 577: return " аяты: ";
            case 578: return " аяты: ";
            case 579: return " аяты: ";
            case 580: return " аяты: ";
            case 581: return " аяты: ";
            case 582: return " аяты: ";
            case 583: return " аяты: ";
            case 584: return " аяты: ";
            case 585: return " аяты: ";
            case 586: return " аяты: ";
            case 587: return " аяты: ";
            case 588: return " аяты: ";
            case 589: return " аяты: ";
            case 590: return " аяты: ";
            case 591: return " аяты: ";
            case 592: return " аяты: ";
            case 593: return " аяты: ";
            case 594: return " аяты: ";
            case 595: return " аяты: ";
            case 596: return " аяты: ";
            case 597: return " аяты: ";
            case 598: return " аяты: ";
            case 599: return " аяты: ";
            case 600: return " аяты: ";
            case 601: return " аяты: ";
            case 602: return " аяты: ";
            case 603: return " аяты: ";
            case 604: return " аяты: ";
            default: return "";
        }
    }

    private String getSuraTitle(int page) {
        switch (page) {
            case 1:
                return "Сура 1. Аль-Фатиха - Открывающая";
            case 2: case 3: case 4: case 5: case 6:
            case 7: case 8: case 9: case 10: case 11:
            case 12: case 13: case 14: case 15: case 16:
            case 17: case 18: case 19: case 20: case 21:
            case 22: case 23: case 24: case 25: case 26:
            case 27: case 28: case 29: case 30: case 31:
            case 32: case 33: case 34: case 35: case 36:
            case 37: case 38: case 39: case 40: case 41:
            case 42: case 43: case 44: case 45: case 46:
            case 47: case 48: case 49:
                return "Сура 2. Аль-Бакара - Корова";
            case 50: case 51: case 52: case 53: case 54: case 55:
            case 56: case 57: case 58: case 59: case 60:
            case 61: case 62: case 63: case 64: case 65:
            case 66: case 67: case 68: case 69: case 70:
            case 71: case 72: case 73: case 74: case 75:
            case 76:
                return "Сура 3. Аль-Имран - Семейство Имрана";
            case 77: case 78: case 79: case 80: case 81:
            case 82: case 83: case 84: case 85: case 86:
            case 87: case 88: case 89: case 90: case 91:
            case 92: case 93: case 94: case 95: case 96:
            case 97: case 98: case 99: case 100: case 101:
            case 102: case 103: case 104: case 105:
                return "Сура 4. Ан-Ниса - Женщины";
            case 106: return "Сура 4. Ан-Ниса - Женщины, аяты: 176, \nСура 5. Аль-Маида - Трапеза, аяты 1-2";
            case 107: case 108:
            case 109: case 110: case 111: case 112: case 113:
            case 114: case 115: case 116: case 117: case 118:
            case 119: case 120: case 121: case 122: case 123: case 124:
            case 125: case 126: case 127:
                return "Сура 5. Аль-Маида - Трапеза";
            case 128: case 129: case 130: case 131: case 132:
            case 133: case 134: case 135: case 136: case 137:
            case 138: case 139: case 140: case 141: case 142:
            case 143: case 144: case 145: case 146: case 147:
            case 148: case 149: case 150:
                return "Сура 6 Аль-Анам - Скот";
            case 151: case 152: case 153: case 154:
            case 155: case 156: case 157: case 158: case 159:
            case 160: case 161: case 162: case 163: case 164:
            case 165: case 166: case 167: case 168: case 169:
            case 170: case 171: case 172: case 173: case 174:
            case 175: case 176:
                return "Сура 7. Аль-Араф - Преграды";
            case 177: case 178: case 179: case 180: case 181:
            case 182: case 183: case 184: case 185: case 186:
                return "Сура 8. Аль-Анфаль - Добыча";
            case 187: case 188: case 189: case 190: case 191:
            case 192: case 193: case 194: case 195: case 196:
            case 197: case 198: case 199: case 200: case 201:
            case 202: case 203: case 204: case 205: case 206:
            case 207:
                return "Сура 9. Ат-Тауба - Покаяние";
            case 208: case 209: case 210: case 211:
            case 212: case 213: case 214: case 215: case 216:
            case 217: case 218: case 219: case 220:
                return "Сура 10. Юнус - Пророк Юнус, мир ему";
            case 221: case 222: case 223: case 224:
            case 225: case 226: case 227: case 228:
            case 229: case 230: case 231: case 232:
            case 233: case 234:
                return "Сура 11. Худ - Пророк Худ, мир ему";
            case 235: case 236: case 237: case 238:
            case 239: case 240: case 241: case 242:
            case 243: case 244: case 245: case 246:
            case 247: case 248:
                return "Сура 12. Йусуф - Пророк Йусуф, мир ему";
            case 249: case 250: case 251: case 252:
            case 253: case 254:
                return "Сура 13. Ар-Ра'д - Гром";
            case 255: case 256: case 257: case 258:
            case 259: case 260: case 261:
                return "Сура 14. Ибрахим - Пророк Ибрахим, мир ему";
            case 262:
            case 263:
            case 264:
            case 265:
            case 266:
                return "Сура 15. Аль-Хиджр - Каменное плато";
            case 267:
            case 268:
            case 269:
            case 270:
            case 271:
            case 272:
            case 273:
            case 274:
            case 275:
            case 276:
            case 277:
            case 278:
            case 279:
            case 280:
            case 281:
                return "Сура 16. Ан-Нахль - Пчелы";
            case 282:
            case 283:
            case 284:
            case 285:
            case 286:
            case 287:
            case 288:
            case 289:
            case 290:
            case 291:
            case 292:
                return "Сура 17. Аль-Исра - Ночной перенос";
            case 293:
            case 294:
            case 295:
            case 296:
            case 297:
            case 298:
            case 299:
            case 300:
            case 301:
            case 302:
            case 303:
            case 304:
                return "Сура 18. Аль-Кахф - Пещера";
            case 305:
            case 306:
            case 307:
            case 308:
            case 309:
            case 310:
            case 311:
                return "Сура 19. Марьям - Марьям (Мария)";
            case 312:
            case 313:
            case 314:
            case 315:
            case 316:
            case 317:
            case 318:
            case 319:
            case 320:
            case 321:
                return "Сура 20. Та-Ха - Та-Ха";
            case 322:
            case 323:
            case 324:
            case 325:
            case 326:
            case 327:
            case 328:
            case 329:
            case 330:
            case 331:
                return "Сура 21. Аль-Анбия - Пророки";
            case 332:
            case 333:
            case 334:
            case 335:
            case 336:
            case 337:
            case 338:
            case 339:
            case 340:
            case 341:
                return "Сура 22. Аль-Хадж - Паломничество";
            case 342:
            case 343:
            case 344:
            case 345:
            case 346:
            case 347:
            case 348:
            case 349:
                return "Сура 23. Аль-Му'минун - Верующие";
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:
            case 356:
            case 357:
            case 358:
                return "Сура 24. Ан-Нур - Свет";
            case 359:
            case 360:
            case 361:
            case 362:
            case 363:
            case 364:
            case 365:
            case 366:
                return "Сура 25. Аль-Фуркан - Различение";
            case 367:
            case 368:
            case 369:
            case 370:
            case 371:
            case 372:
            case 373:
            case 374:
            case 375:
            case 376:
                return "Сура 26. Аш-Шуара - Поэты";
            case 377:
            case 378:
            case 379:
            case 380:
            case 381:
            case 382:
            case 383:
            case 384:
                return "Сура 27. Ан-Намль - Муравьи";
            case 385:
            case 386:
            case 387:
            case 388:
            case 389:
            case 390:
            case 391:
            case 392:
            case 393:
            case 394:
            case 395:
                return "Сура 28. Аль-Касас - Рассказы";
            case 396:
            case 397:
            case 398:
            case 399:
            case 400:
            case 401:
            case 402:
            case 403:
                return "Сура 29. Аль-Анкабут - Паук";
            case 404:
            case 405:
            case 406:
            case 407:
            case 408:
            case 409:
            case 410:
                return "Сура 30. Ар-Рум - Римляне";
            case 411:
            case 412:
            case 413:
            case 414:
                return "Сура 31. Лукман - Лукман";
            case 415:
            case 416:
            case 417:
                return "Сура 32. Ас-Саджда - Поклон";
            case 418:
            case 419:
            case 420:
            case 421:
            case 422:
            case 423:
            case 424:
            case 425:
            case 426:
            case 427:
                return "Сура 33. Аль-Ахзаб - Союзники";
            case 428:
            case 429:
            case 430:
            case 431:
            case 432:
            case 433:
                return "Сура 34. Саба - Сабейцы";
            case 434:
            case 435:
            case 436:
            case 437:
            case 438:
            case 439:
                return "Сура 35. Фатыр - Творец";
            case 440:
            case 441:
            case 442:
            case 443:
            case 444:
            case 445:
                return "Сура 36. Йа Син - Йа Син";
            case 446:
            case 447:
            case 448:
            case 449:
            case 450:
            case 451:
            case 452:
                return "Сура 37. Ас-Саффат - Выстраивающиеся в ряды";
            case 453:
            case 454:
            case 455:
            case 456:
            case 457:
                return "Сура 38. Сад - Буква Сад";
            case 458:
            case 459:
            case 460:
            case 461:
            case 462:
            case 463:
            case 464:
            case 465:
            case 466:
                return "Сура 39. Аз-Зумар - Толпы";
            case 467:
            case 468:
            case 469:
            case 470:
            case 471:
            case 472:
            case 473:
            case 474:
            case 475:
            case 476:
                return "Сура 40. Гафир - Прощающий";
            case 477:
            case 478:
            case 479:
            case 480:
            case 481:
            case 482:
                return "Сура 41. Фуссылят - Разъяснены";
            case 483:
            case 484:
            case 485:
            case 486:
            case 487:
            case 488:
                return "Сура 42. Аш-Шура - Совет";
            case 489:
            case 490:
            case 491:
            case 492:
            case 493:
            case 494:
            case 495:
                return "Сура 43. Аз-Зухруф - Украшения";
            case 496:
            case 497:
            case 498:
                return "Сура 44. Ад-Духан - Дым";
            case 499:
            case 500:
            case 501:
                return "Сура 45. Аль-Джасия - Коленопреклоненные";
            case 502:
            case 503:
            case 504:
            case 505:
            case 506:
                return "Сура 46. Аль-Ахкаф - Дюны";
            case 507:
            case 508:
            case 509:
            case 510:
                return "Сура 47. Мухаммад - Пророк Мухаммад, мир ему и благословение Всевышнего";
            case 511:
            case 512:
            case 513:
            case 514:
                return "Сура 48. Аль-Фатх - Победа";
            case 515:
            case 516:
            case 517:
                return "Сура 49. Аль-Худжурат - Комнаты";
            case 518:
            case 519:
                return "Сура 50. Каф - Буква Каф";
            case 520:
            case 521:
            case 522:
                return "Сура 51. Аль-Дариат - Рассеивающие";
            case 523:
            case 524:
            case 525:
                return "Сура 52. Ат-Тур - Гора";
            case 526:
            case 527:
                return "Сура 53. Ан-Наджм - Звезда";
            case 528:
            case 529:
            case 530:
                return "Сура 54. Аль-Камар - Луна";
            case 531:
            case 532:
            case 533:
                return "Сура 55. Ар-Рахман - Милостивый";
            case 534:
            case 535:
            case 536:
                return "Сура 56. Аль-Вакы'а - Неотвратимое событие";
            case 537:
            case 538:
            case 539:
            case 540:
            case 541:
                return "Сура 57. Аль-Хадид - Железо";
            case 542:
            case 543:
            case 544:
                return "Сура 58. Аль-Муджадала - Препирающаяся";
            case 545:
            case 546:
            case 547:
            case 548:
                return "Сура 59. Аль-Хашр - Сбор";
            case 549:
            case 550:
                return "Сура 60. Аль-Мумтахина - Испытуемые";
            case 551:
            case 552:
                return "Сура 61. Ас-Сафф - Ряд";
            case 553:
                return "Сура 62. Аль-Джуму'а - Пятница";
            case 554:
            case 555:
                return "Сура 63. Аль-Мунафикун - Лицемеры";
            case 556:
            case 557:
                return "Сура 64. Ат-Тагабун - Раскрытие самообмана";
            case 558:
            case 559:
                return "Сура 65. Ат-Талак - Развод";
            case 560:
            case 561:
                return "Сура 66. Ат-Тахрим - Запрещение";
            case 562:
            case 563:
                return "Сура 67. Аль-Мульк - Власть";
            case 564:
            case 565:
                return "Сура 68. Аль-Калам - Письменная трость";
            case 566:
            case 567:
                return "Сура 69. Аль-Хакка - Неизбежное";
            case 568:
            case 569:
                return "Сура 70. Аль-Ма'аридж - Ступени";
            case 570:
            case 571:
                return "Сура 71. Нух - Нух (Ной)";
            case 572:
            case 573:
                return "Сура 72. Аль-Джинн - Джинны";
            case 574:
                return "Сура 73. Аль-Муззаммиль - Закутавшийся";
            case 575:
            case 576:
                return "Сура 74. Аль-Муддассир - Завернувшийся";
            case 577:
                return "Сура 75. Аль-Кыяма - Воскресение";
            case 578:
            case 579:
                return "Сура 76. Аль-Инсан - Человек";
            case 580:
            case 581:
                return "Сура 77. Аль-Мурсалят - Посланные";
            case 582:
                return "Сура 78. Ан-Наба - Весть";
            case 583:
            case 584:
                return "Сура 79. Ан-Назиат - Вырывающие";
            case 585:
                return "Сура 80. Абаса - Нахмурившийся";
            case 586:
                return "Сура 81. Ат-Таквир - Скручивание";
            case 587: return "Сура 82. Аль-Инфитар - Раскалывание \n Сура 83. Аль-Мутаффифин - Обвешивающие";
            case 588: return "Сура 83. Аль-Мутаффифин - Обвешивающие";
            case 589: return "Сура 84. Аль-Иншикак - Разверзнется";
            case 590: return "Сура 85. Аль-Бурудж - Созвездия";
            case 591: return "Сура 86. Ат-Тарик - Ночной путник \n Сура 87. Аль-А'ля - Высочайший";
            case 592: return "Сура 88. Аль-Гашия - Покрывающее";
            case 593: return "Сура 89. Аль-Фаджр - Заря";
            case 594: return "Сура 90. Аль-Баляд - Город";
            case 595: return "Сура 91. Аш-Шамс - Солнце \n Сура 92. Аль-Ляйл - Ночь";
            case 596: return "Сура 93. Ад-Духа - Утро \n Сура 94. Аш-Шарх - Раскрытие";
            case 597: return "Сура 95. Ат-Тин - Инжир \n Сура 96. Аль-'Аляк - Сгусток крови";
            case 598: return "Сура 97. Аль-Кадр - Ночь предопределения \n Сура 98. Аль-Баййина - Ясное знамение";
            case 599: return "Сура 99. Аз-Зальзаля - Землетрясение \n Сура 100. Аль-'Адият - Скачущие";
            case 600: return "Сура 101. Аль-Кари'а - Великое бедствие \n Сура 102. Ат-Такасур - Страсть к приумножению";
            case 601: return "Сура 103. Аль-'Аср - Предвечернее время \n Сура 104. Аль-Хумаза - Хулитель \n Сура 105. Аль-Филь - Слон";
            case 602: return "Сура 106. Курайш - Курайшиты \n Сура 107. Аль-Ма'ун - Мелочь \n Сура 108. Аль-Кяусар - Изобилие";
            case 603: return "Сура 109. Аль-Кяфирун - Неверующие \n Сура 110. Ан-Наср - Победа \n Сура 111. Аль-Ляхаб - Пальмовые волокна";
            case 604: return "Сура 112. Аль-Ихляс - Искренность \n Сура 113. Аль-Фаляк - Рассвет \n Сура 114. Ан-Нас - Люди";

            default:
                return "Страница не найдена";
        }
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
        public MaterialButton deleteBookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Инициализация элементов интерфейса
            textView = itemView.findViewById(R.id.bookmarkText);
            deleteBookmark = itemView.findViewById(R.id.deleteBookmark);
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

    /*private int getSuraIndex(int page) {
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
    }*/


    public static Map<String, Integer> createSuraMap(String[] sures, int[] numPageSures) {
        // Создаем словарь, где ключи - названия сур, значения - начальные страницы сур
        Map<String, Integer> suraMap = new HashMap<>();

        // Предполагаем, что sures и numPageSures имеют одинаковую длину
        for (int i = 0; i < sures.length; i++) {
            // Добавляем каждую суру и ее начальную страницу в словарь
            suraMap.put(sures[i], numPageSures[i]);
        }

        return suraMap;
    }


    /*
     * Задачи:
     * 1. Реализовать перемещение к закладке (при нажатии на закладку перемещаться во ViewPager2)
     * к заданной странице
     * 2. Исправить ошибку с закладками (для каждой позиции должна быть отдельная закладка)
     */

}