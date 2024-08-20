package raf.tabiin.qurantajweed.adapters;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
            "Сура 51. Аз-Зарият — Рассеивающие",
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
        });
    }

    public String getAyatsOnPage(int page) {
        switch (page) {
            //Сура 1 Аль-Фатиха - Открывающая
            case 1: return " аяты: 1-7";

            //Сура 2 Аль-Бакара - Корова
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

            //Сура 3 Аль-Имран - Семейство Имрана
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

            //Сура 4 Ан-Ниса - Женщины
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

            case 106: return "";

            // Сура 5 Аль-Маида - Трапеза
            case 107: return " аяты: 3-5";
            case 108: return " аяты: 6-9";
            case 109: return " аяты: 10-13";
            case 110: return " аяты: 14-17";
            case 111: return " аяты: 18-23";
            case 112: return " аяты: 24-31";
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

            //Сура 6 Аль-Ан'ам - Скот
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

            //Сура 7 Аль-А'раф - Преграды
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
            case 171: return " аяты: 160-163";
            case 172: return " аяты: 164-170";
            case 173: return " аяты: 171-178";
            case 174: return " аяты: 179-187";
            case 175: return " аяты: 188-195";
            case 176: return " аяты: 196-206, Саджда-Тилява после 206-го аята";

            // Сура 8 Аль-Анфаль - Трофеи
            case 177: return " аяты: 1-8";
            case 178: return " аяты: 9-16";
            case 179: return " аяты: 17-25";
            case 180: return " аяты: 26-33";
            case 181: return " аяты: 34-40";
            case 182: return " аяты: 41-45";
            case 183: return " аяты: 46-52";
            case 184: return " аяты: 53-61";
            case 185: return " аяты: 62-69";
            case 186: return " аяты: 70-75";

            // Сура 9 Ат-Тауба - Покаяние
            case 187: return " аяты: 1-6";
            case 188: return " аяты: 7-13";
            case 189: return " аяты: 14-20";
            case 190: return " аяты: 21-26";
            case 191: return " аяты: 27-31";
            case 192: return " аяты: 32-36";
            case 193: return " аяты: 37-40";
            case 194: return " аяты: 41-47";
            case 195: return " аяты: 48-53";
            case 196: return " аяты: 54-61";
            case 197: return " аяты: 62-68";
            case 198: return " аяты: 69-72";
            case 199: return " аяты: 73-79";
            case 200: return " аяты: 80-86";
            case 201: return " аяты: 87-93";
            case 202: return " аяты: 94-99";
            case 203: return " аяты: 100-106";
            case 204: return " аяты: 107-111";
            case 205: return " аяты: 112-117";
            case 206: return " аяты: 118-122";
            case 207: return " аяты: 123-129";

            //Сура 10 Юнус - Пророк Юнус, мир ему
            case 208: return " аяты: 1-6";
            case 209: return " аяты: 7-14";
            case 210: return " аяты: 15-20";
            case 211: return " аяты: 21-25";
            case 212: return " аяты: 26-33";
            case 213: return " аяты: 34-42";
            case 214: return " аяты: 43-53";
            case 215: return " аяты: 54-61";
            case 216: return " аяты: 62-70";
            case 217: return " аяты: 71-78";
            case 218: return " аяты: 79-88";
            case 219: return " аяты: 89-97";
            case 220: return " аяты: 98-106";

            case 221: return "";

            // Сура 11 Худ - Пророк Худ
            case 222: return " аяты: 6-12";
            case 223: return " аяты: 13-19";
            case 224: return " аяты: 20-28";
            case 225: return " аяты: 29-37";
            case 226: return " аяты: 38-45";
            case 227: return " аяты: 46-53";
            case 228: return " аяты: 54-62";
            case 229: return " аяты: 63-71";
            case 230: return " аяты: 72-81";
            case 231: return " аяты: 82-88";
            case 232: return " аяты: 89-97";
            case 233: return " аяты: 98-108";
            case 234: return " аяты: 109-117";

            case 235: return "";

            // Сура 12 Юсуф - Пророк Юсуф, мир ему
            case 236: return " аяты: 5-14";
            case 237: return " аяты: 15-22";
            case 238: return " аяты: 23-30";
            case 239: return " аяты: 31-37";
            case 240: return " аяты: 38-43";
            case 241: return " аяты: 44-52";
            case 242: return " аяты: 53-63";
            case 243: return " аяты: 64-69";
            case 244: return " аяты: 70-78";
            case 245: return " аяты: 79-86";
            case 246: return " аяты: 87-95";
            case 247: return " аяты: 96-103";
            case 248: return " аяты: 104-111";

            // Сура 13 Ар-Рад - Гром
            case 249: return " аяты: 1-5";
            case 250: return " аяты: 6-13";
            case 251: return " аяты: 14-18, Саджда-Тилява после 15-го аята";
            case 252: return " аяты: 19-28";
            case 253: return " аяты: 29-34";
            case 254: return " аяты: 35-42";

            case 255: return "";

            // Сура 14 Ибрахим - Пророк Ибрахим, мир ему
            case 256: return " аяты: 6-10";
            case 257: return " аяты: 11-18";
            case 258: return " аяты: 19-24";
            case 259: return " аяты: 25-33";
            case 260: return " аяты: 34-42";
            case 261: return " аяты: 43-52";

            // Сура 15 Аль-Хиджр - Каменное плато
            case 262: return " аяты: 1-15";
            case 263: return " аяты: 16-31";
            case 264: return " аяты: 32-51";
            case 265: return " аяты: 52-70";
            case 266: return " аяты: 71-90";

            case 267: return "";

            // Сура 16 Ан-Нахль - Пчелы
            case 268: return " аяты: 7-14";
            case 269: return " аяты: 15-26";
            case 270: return " аяты: 27-34";
            case 271: return " аяты: 35-42";
            case 272: return " аяты: 43-54, Саджда-Тилява после 50-го аята";
            case 273: return " аяты: 55-64";
            case 274: return " аяты: 65-72";
            case 275: return " аяты: 73-79";
            case 276: return " аяты: 80-87";
            case 277: return " аяты: 88-93";
            case 278: return " аяты: 94-102";
            case 279: return " аяты: 103-110";
            case 280: return " аяты: 111-118";
            case 281: return " аяты: 119-128";

            // Сура 17 Аль-Исра - Ночной перенос
            case 282: return " аяты: 1-7";
            case 283: return " аяты: 8-17";
            case 284: return " аяты: 18-27";
            case 285: return " аяты: 28-38";
            case 286: return " аяты: 39-49";
            case 287: return " аяты: 50-58";
            case 288: return " аяты: 59-66";
            case 289: return " аяты: 67-75";
            case 290: return " аяты: 76-86";
            case 291: return " аяты: 87-96";
            case 292: return " аяты: 97-104";

            case 293: return "";

            // Сура 18 Аль-Кяхф - Пещера
            case 294: return " аяты: 5-15";
            case 295: return " аяты: 16-20";
            case 296: return " аяты: 21-27";
            case 297: return " аяты: 28-34";
            case 298: return " аяты: 35-45";
            case 299: return " аяты: 46-53";
            case 300: return " аяты: 54-61";
            case 301: return " аяты: 62-74";
            case 302: return " аяты: 75-83";
            case 303: return " аяты: 84-97";
            case 304: return " аяты: 98-110";

            // Сура 19 Марьям
            case 305: return " аяты: 1-11";
            case 306: return " аяты: 12-25";
            case 307: return " аяты: 26-38";
            case 308: return " аяты: 39-51";
            case 309: return " аяты: 52-64, Саджда-Тилява после 58-го аята";
            case 310: return " аяты: 65-76";
            case 311: return " аяты: 77-95";

            case 312: return "";

            // Сура 20 Та Ха
            case 313: return " аяты: 13-37";
            case 314: return " аяты: 38-51";
            case 315: return " аяты: 52-64";
            case 316: return " аяты: 65-76";
            case 317: return " аяты: 77-87";
            case 318: return " аяты: 88-98";
            case 319: return " аяты: 99-113";
            case 320: return " аяты: 114-125";
            case 321: return " аяты: 126-135";

            // Сура 21 Аль-Анбия - Пророки
            case 322: return " аяты: 1-10";
            case 323: return " аяты: 11-24";
            case 324: return " аяты: 25-35";
            case 325: return " аяты: 36-44";
            case 326: return " аяты: 45-57";
            case 327: return " аяты: 58-72";
            case 328: return " аяты: 73-81";
            case 329: return " аяты: 82-90";
            case 330: return " аяты: 91-101";
            case 331: return " аяты: 102-112";

            // Сура 22 Аль-Хадж
            case 332: return " аяты: 1-5";
            case 333: return " аяты: 6-15";
            case 334: return " аяты: 16-23, Саджда-Тилява после 18-го аята";
            case 335: return " аяты: 24-30";
            case 336: return " аяты: 31-38";
            case 337: return " аяты: 39-46";
            case 338: return " аяты: 47-55";
            case 339: return " аяты: 56-64";
            case 340: return " аяты: 65-72";
            case 341: return " аяты: 73-78, Саджда-Тилява после 77-го аята";

            // Сура 23 Аль-Му'минун - Верующие
            case 342: return " аяты: 1-17";
            case 343: return " аяты: 18-27";
            case 344: return " аяты: 28-42";
            case 345: return " аяты: 43-59";
            case 346: return " аяты: 60-74";
            case 347: return " аяты: 75-89";
            case 348: return " аяты: 90-104";
            case 349: return " аяты: 105-118";

            // Сура 24 Ан-Нур - Свет
            case 350: return " аяты: 1-10";
            case 351: return " аяты: 11-20";
            case 352: return " аяты: 21-27";
            case 353: return " аяты: 28-31";
            case 354: return " аяты: 32-36";
            case 355: return " аяты: 37-43";
            case 356: return " аяты: 44-53";
            case 357: return " аяты: 54-58";
            case 358: return " аяты: 59-61";

            case 359: return "";

            // Сура 25 Аль-Фуркан - Различение
            case 360: return " аяты: 3-11";
            case 361: return " аяты: 12-20";
            case 362: return " аяты: 21-32";
            case 363: return " аяты: 33-43";
            case 364: return " аяты: 44-55";
            case 365: return " аяты: 56-67, Саджда-Тилява после 60-го аята";
            case 366: return " аяты: 68-77";

            // Сура 26 Аш-Шуара - Поэты
            case 367: return " аяты: 1-19";
            case 368: return " аяты: 20-39";
            case 369: return " аяты: 40-60";
            case 370: return " аяты: 61-83";
            case 371: return " аяты: 84-111";
            case 372: return " аяты: 112-136";
            case 373: return " аяты: 137-159";
            case 374: return " аяты: 160-183";
            case 375: return " аяты: 184-206";
            case 376: return " аяты: 207-227";

            // Сура 27 Ан-Намль - Муравьи
            case 377: return " аяты: 1-13";
            case 378: return " аяты: 14-22";
            case 379: return " аяты: 23-35, Саджда-Тилява после 26-го аята";
            case 380: return " аяты: 36-44";
            case 381: return " аяты: 45-55";
            case 382: return " аяты: 56-63";
            case 383: return " аяты: 64-76";
            case 384: return " аяты: 77-88";

            case 385: return "";

            // Сура 28 Аль-Касас - Рассказы
            case 386: return " аяты: 6-13";
            case 387: return " аяты: 14-21";
            case 388: return " аяты: 22-28";
            case 389: return " аяты: 29-35";
            case 390: return " аяты: 36-43";
            case 391: return " аяты: 44-50";
            case 392: return " аяты: 51-59";
            case 393: return " аяты: 60-70";
            case 394: return " аяты: 71-77";
            case 395: return " аяты: 78-84";

            case 396: return "";

            // Сура 29 Аль-Анкабут - Паук
            case 397: return " аяты: 7-14";
            case 398: return " аяты: 15-23";
            case 399: return " аяты: 24-30";
            case 400: return " аяты: 31-38";
            case 401: return " аяты: 39-45";
            case 402: return " аяты: 46-52";
            case 403: return " аяты: 53-63";

            case 404: return "";

            // Сура 30 Ар-Рум - Римляне
            case 405: return " аяты: 6-15";
            case 406: return " аяты: 16-24";
            case 407: return " аяты: 25-32";
            case 408: return " аяты: 33-41";
            case 409: return " аяты: 42-50";
            case 410: return " аяты: 51-60";

            // Сура 31 Лукман
            case 411: return " аяты: 1-11";
            case 412: return " аяты: 12-19";
            case 413: return " аяты: 20-28";
            case 414: return " аяты: 29-34";

            // Сура 32 Ас-Саджда - Земной поклон
            case 415: return " аяты: 1-11";
            case 416: return " аяты: 12-20, Саджда-Тилява после 15-го аята";
            case 417: return " аяты: 21-30";

            // Сура 33 Аль-Ахзаб - Союзники
            case 418: return " аяты: 1-6";
            case 419: return " аяты: 7-15";
            case 420: return " аяты: 16-22";
            case 421: return " аяты: 23-30";
            case 422: return " аяты: 31-35";
            case 423: return " аяты: 36-43";
            case 424: return " аяты: 44-50";
            case 425: return " аяты: 51-54";
            case 426: return " аяты: 55-62";
            case 427: return " аяты: 63-73";

            // Сура 34 Саба - Сабейцы
            case 428: return " аяты: 1-7";
            case 429: return " аяты: 8-14";
            case 430: return " аяты: 15-22";
            case 431: return " аяты: 23-31";
            case 432: return " аяты: 32-39";
            case 433: return " аяты: 40-48";

            case 434: return "";

            // Сура 35 Фатыр - Творец
            case 435: return " аяты: 4-11";
            case 436: return " аяты: 12-18";
            case 437: return " аяты: 19-30";
            case 438: return " аяты: 31-38";
            case 439: return " аяты: 39-44";

            case 440: return "";

            // Сура 36 Йа Син
            case 441: return " аяты: 13-27";
            case 442: return " аяты: 28-40";
            case 443: return " аяты: 41-54";
            case 444: return " аяты: 55-70";
            case 445: return " аяты: 71-83";

            //Сура 37 Ас-Саффат - Выстраивающиеся в ряды
            case 446: return " аяты: 1-24";
            case 447: return " аяты: 25-51";
            case 448: return " аяты: 52-76";
            case 449: return " аяты: 77-102";
            case 450: return " аяты: 103-126";
            case 451: return " аяты: 127-153";
            case 452: return " аяты: 154-182";

            //Сура 38 Сад - буква Сад
            case 453: return " аяты: 1-16";
            case 454: return " аяты: 17-26";
            case 455: return " аяты: 18-42";
            case 456: return " аяты: 43-61";
            case 457: return " аяты: 62-83";

            case 458: return "";

            // Сура 39 - Аз-Зумар - Толпы
            case 459: return " аяты: 6-10";
            case 460: return " аяты: 11-21";
            case 461: return " аяты: 22-31";
            case 462: return " аяты: 32-40";
            case 463: return " аяты: 41-47";
            case 464: return " аяты: 48-56";
            case 465: return " аяты: 57-67";
            case 466: return " аяты: 68-74";

            case 467: return "";

            // Сура 40 - Аль-Гафир - Прощающий
            case 468: return " аяты: 8-16";
            case 469: return " аяты: 17-25";
            case 470: return " аяты: 26-33";
            case 471: return " аяты: 34-40";
            case 472: return " аяты: 41-49";
            case 473: return " аяты: 50-58";
            case 474: return " аяты: 59-66";
            case 475: return " аяты: 67-77";
            case 476: return " аяты: 78-85";

            // Сура 41 Фуссылят - Разъяснены
            case 477: return " аяты: 1-11";
            case 478: return " аяты: 12-20";
            case 479: return " аяты: 21-29";
            case 480: return " аяты: 30-38, Саджда-Тилява после 38-го аята";
            case 481: return " аяты: 39-46";
            case 482: return " аяты: 47-54";

            // Сура 42 Аш-Шура - Совет
            case 483: return " аяты: 1-10";
            case 484: return " аяты: 11-15";
            case 485: return " аяты: 16-22";
            case 486: return " аяты: 23-31";
            case 487: return " аяты: 32-44";
            case 488: return " аяты: 45-51";

            case 489: return "";

            // Сура 43 Аз-Зухруф - Украшения
            case 490: return " аяты: 11-22";
            case 491: return " аяты: 23-33";
            case 492: return " аяты: 34-47";
            case 493: return " аяты: 48-60";
            case 494: return " аяты: 61-73"; //TODO
            case 495: return " аяты: 74-89";

            //Сура 44 - Ад-Духан - Дым
            case 496: return " аяты: 1-18";
            case 497: return " аяты: 19-39";
            case 498: return " аяты: 40-59";

            //Сура 45 - Аль-Джасия - Коленопреклоненные
            case 499: return " аяты: 1-13";
            case 500: return " аяты: 14-22";
            case 501: return " аяты: 23-32";

            case 502: return "";

            // Сура 46 - Аль-Ахкаф - Пески
            case 503: return " аяты: 6-14";
            case 504: return " аяты: 15-20";
            case 505: return " аяты: 21-28";
            case 506: return " аяты: 29-35";

            // Сура 47 - Мухаммад - Пророк Мухаммад, мир ему и благословение Аллаха
            case 507: return " аяты: 1-11";
            case 508: return " аяты: 12-19";
            case 509: return " аяты: 20-29";
            case 510: return " аяты: 30-38";

            // Сура 48 - Аль-Фатх - Победа
            case 511: return " аяты: 1-9";
            case 512: return " аяты: 10-15";
            case 513: return " аяты: 16-23";
            case 514: return " аяты: 24-28";

            case 515: return "";

            // Сура 49 - Аль-Худжурат - Комнаты
            case 516: return " аяты: 5-11";
            case 517: return " аяты: 12-18";

            // Сура 50 - Каф - буква Каф
            case 518: return " аяты: 1-15";
            case 519: return " аяты: 16-35";
            case 520: return "";

            // Сура 51 Аз-Зарият
            case 521: return " аяты: 7-30";
            case 522: return " аяты: 31-51";
            case 523: return "";

            // Сура 52 - Ат-Тур - Гора
            case 524: return " аяты: 15-31";
            case 525: return " аяты: 32-49";

            // Сура 53 Ан-Наджм - Звезда
            case 526: return " аяты: 1-26";
            case 527: return " аяты: 27-44";

            case 528: return "";

            // Сура 54
            case 529: return " аяты: 7-27";
            case 530: return " аяты: 28-49";

            case 531: return "";

            // Сура 55 - Ар-Рахман - Милостивый
            case 532: return " аяты: 17-40";
            case 533: return " аяты: 41-67";

            case 534: return "";

            // Сура 56 - Аль-Вакиа - Неотвратимое событие
            case 535: return " аяты: 17-50";
            case 536: return " аяты: 51-76";

            case 537: return "";

            // Сура 57 - Аль-Хадид - Железо
            case 538: return " аяты: 4-11";
            case 539: return " аяты: 12-18";
            case 540: return " аяты: 19-24";
            case 541: return " аяты: 25-29";

            // Сура 58 - Аль-Муджадаля - Препирающаяся
            case 542: return " аяты: 1-6";
            case 543: return " аяты: 7-11";
            case 544: return " аяты: 12-21";

            case 545: return "";

            // Сура 59 - Аль-Хашр - Сбор
            case 546: return " аяты: 4-9";
            case 547: return " аяты: 10-16";
            case 548: return " аяты: 17-24";

            // Сура 60 - Аль-Мумтахина - Испытуемая
            case 549: return " аяты: 1-5";
            case 550: return " аяты: 6-11";

            case 551: return "";

            // Сура 61 - Ас-Саф - Ряд
            case 552: return " аяты: 6-14";

            // Сура 62 - Аль-Джуму'а - Пятница
            case 553: return " аяты: 1-8";

            case 554: return "";

            // Сура 63 - Аль-Мунаффикун - Лицемеры
            case 555: return " аяты: 5-11";

            // Сура 64 - Ат-Тагабун - Раскрытие самообмана
            case 556: return " аяты: 1-9";
            case 557: return " аяты: 10-18";

            // Сура 65 - Ат-Таляк - развод
            case 558: return " аяты: 1-5";
            case 559: return " аяты: 6-12";

            // Сура 66 - Ат-Тахрим - Запрещение
            case 560: return " аяты: 1-7";
            case 561: return " аяты: 8-12";

            // Сура 67 - Аль-Мульк - Власть
            case 562: return " аяты: 1-12";
            case 563: return " аяты: 13-26";

            case 564: return "";

            // Сура 68 - Аль-Калям - Письменная трость
            case 565: return " аяты: 16-42";

            case 566: return "";

            // Сура 69 - Аль-Хакка - Неизбежное
            case 567: return " аяты: 9-34";

            case 568: return "";

            // Сура 70 - Аль-Ма'аридж - Ступени
            case 569: return " аяты: 11-39";

            case 570: return "";

            // Сура 71 - Нух - Пророк Нух, мир ему
            case 571: return " аяты: 11-28";

            // Сура 72 - Аль-Джинн - Джинны
            case 572: return " аяты: 1-13";
            case 573: return " аяты: 14-28";

            // Сура 73 - Аль-Музаммиль - Завернувшийся
            case 574: return " аяты: 1-19";

            case 575: return "";

            // Сура 74 - Аль-Мудассир - Закутавшийся
            case 576: return " аяты: 18-47";

            case 577: return "";

            // Сура 75 - Аль-Кыяма - Воскресение
            case 578: return "";

            // Сура 76 - Аль-Инсан - Человек
            case 579: return " аяты: 6-25";

            case 580: return "";

            // Сура 77 - Аль-Мурсалят - Посланные
            case 581: return " аяты: 20-40";

            // Сура 78 - Ан-Наба - Весть
            case 582: return " аяты: 1-30";

            case 583: return "";

            // Сура 79 - Ан-Назиат - Вырывающие
            case 584: return " аяты: 16-46";

            // Сура 80 - Абаса - Нахмурившийся
            case 585: return " аяты: 1-42";

            // Сура 81 - Ат-Таквир - Погружение во мрак
            case 586: return " аяты: 1-29";

            // Сура 82 - Аль-Инфитар
            case 587: return "";
            case 588: return "";
            case 589: return "";
            case 590: return "";
            case 591: return "";
            case 592: return "";
            case 593: return "";
            case 594: return "";
            case 595: return "";
            case 596: return "";
            case 597: return "";
            case 598: return "";
            case 599: return "";
            case 600: return "";
            case 601: return "";
            case 602: return "";
            case 603: return "";
            case 604: return "";
            default: return "";
        }
    }

    public String getSuraTitle(int page) {
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
            case 106: return "Сура 4. Ан-Ниса - Женщины, аят 176, \nСура 5. Аль-Маида - Трапеза, аяты 1-2";
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
            case 221: return "Сура 10. Юнус - Пророк Юнус, мир ему, аяты 107-109, \nСура 11. Худ - Пророк Худ, мир ему, аяты 1-5";
            case 222: case 223: case 224:
            case 225: case 226: case 227: case 228:
            case 229: case 230: case 231: case 232:
            case 233: case 234:
                return "Сура 11. Худ - Пророк Худ, мир ему";
            case 235: return "Сура 11. Худ - Пророк Худ, мир ему, аяты 118-123, \nСура 12. Йусуф - Пророк Йусуф, мир ему, аяты 1-4";
            case 236: case 237: case 238:
            case 239: case 240: case 241: case 242:
            case 243: case 244: case 245: case 246:
            case 247: case 248:
                return "Сура 12. Йусуф - Пророк Йусуф, мир ему";
            case 249: case 250: case 251: case 252:
            case 253: case 254:
                return "Сура 13. Ар-Ра'д - Гром";
            case 255: return "Сура 13. Ар-Ра'д - Гром, аят 43, \nСура 14. Ибрахим - Пророк Ибрахим, мир ему, аяты 1-5";
            case 256: case 257: case 258:
            case 259: case 260: case 261:
                return "Сура 14. Ибрахим - Пророк Ибрахим, мир ему";
            case 262:
            case 263:
            case 264:
            case 265:
            case 266:
                return "Сура 15. Аль-Хиджр - Каменное плато";
            case 267: return "Сура 15. Аль-Хиджр - Каменное плато, аяты 91-99, \nСура 16. Ан-Нахль - Пчелы, аяты 1-6";
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
            case 293: return "Сура 17. Аль-Исра - Ночной перенос, аяты 105-111, Саджда-Тилява после 109-го аята,\nСура 18. Аль-Кахф - Пещера, аяты 1-4";
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
            case 312: return "Сура 19. Марьям - Марьям (Мария), аяты 96-98, \nСура 20. Та-Ха - Та-Ха, аяты 1-12";
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
            case 359: return "Сура 24. Ан-Нур - Свет, аяты 62-64, \nСура 25. Аль-Фуркан - Различение, аяты 1-2";
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
            case 385: return "Сура 27. Ан-Намль - Муравьи, аяты 89-93, \nСура 28. Аль-Касас - Рассказы, аяты 1-5";
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
            case 396: return "Сура 28. Аль-Касас - Рассказы, аяты 85-88, \nСура 29. Аль-Анкабут - Паук, аяты 1-6";
            case 397:
            case 398:
            case 399:
            case 400:
            case 401:
            case 402:
            case 403:
                return "Сура 29. Аль-Анкабут - Паук";
            case 404: return "Сура 29. Аль-Анкабут - Паук, аяты 64-69, \nСура 30. Ар-Рум - Римляне, аяты 1-5";
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
            case 434: return "Сура 34. Саба - Сабейцы, аяты 49-54, \nСура 35. Фатыр - Творец, аяты 1-3";
            case 435:
            case 436:
            case 437:
            case 438:
            case 439:
                return "Сура 35. Фатыр - Творец";
            case 440: return "Сура 35. Фатыр - Творец, аят 45, \nСура 36. Йа Син - Йа Син, аяты 1-12";
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
            case 458: return "Сура 38. Сад - Буква Сад, аяты 84-88, \nСура 39. Аз-Зумар - Толпы, аяты 1-5";
            case 459:
            case 460:
            case 461:
            case 462:
            case 463:
            case 464:
            case 465:
            case 466:
                return "Сура 39. Аз-Зумар - Толпы";
            case 467: return "Сура 39. Аз-Зумар - Толпы, аят 75, \nСура 40. Гафир - Прощающий, аяты 1-7";
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
            case 489: return "Сура 42. Аш-Шура - Совет, аяты 52-53, \nСура 43. Аз-Зухруф - Украшения, аяты 1-10";
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
            case 502: return "Сура 45. Аль-Джасия - Коленопреклоненные, аяты 33-37, \nСура 46. Аль-Ахкаф - Дюны, аяты 1-5";
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
            case 515: return "Сура 48. Аль-Фатх - Победа, аят 29, \nСура 49. Аль-Худжурат - Комнаты, аяты 1-4";
            case 516:
            case 517:
                return "Сура 49. Аль-Худжурат - Комнаты";
            case 518:
            case 519:
                return "Сура 50. Каф - Буква Каф";
            case 520: return "Сура 50. Каф - Буква Каф, аяты 36-45, \nСура 51. Аз-Зарият - Рассеивающие, аяты 1-6";
            case 521:
            case 522:
                return "Сура 51. Аз-Зарият - Рассеивающие";
            case 523: return "Сура 51. Аз-Зарият - Рассеивающие, аяты 52-60, \nСура 52. Ат-Тур - Гора, аяты 1-14";
            case 524:
            case 525:
                return "Сура 52. Ат-Тур - Гора";
            case 526:
            case 527:
                return "Сура 53. Ан-Наджм - Звезда";
            case 528: return "Сура 53. Ан-Наджм - Звезда, аяты 45-62, Саджда-Тилява после 62-го аята, \nСура 54. Аль-Камар - Луна, аяты 1-6";
            case 529:
            case 530:
                return "Сура 54. Аль-Камар - Луна";
            case 531: return "Сура 54. Аль-Камар - Луна, аяты 50-55, \nСура 55. Ар-Рахман - Милостивый, аяты 1-16";
            case 532:
            case 533:
                return "Сура 55. Ар-Рахман - Милостивый";
            case 534: return "Сура 55. Ар-Рахман - Милостивый, аяты 68-78, \nСура 56. Аль-Вакы'а - Неотвратимое событие - аяты 1-16";
            case 535:
            case 536:
                return "Сура 56. Аль-Вакы'а - Неотвратимое событие";
            case 537: return "Сура 56. Аль-Вакы'а - Неотвратимое событие, аяты 77-96, \nСура 57. Аль-Хадид - Железо, аяты 1-3";
            case 538:
            case 539:
            case 540:
            case 541:
                return "Сура 57. Аль-Хадид - Железо";
            case 542:
            case 543:
            case 544:
                return "Сура 58. Аль-Муджадала - Препирающаяся";
            case 545: return "Сура 58. Аль-Муджадала - Препирающаяся, аят 22, \nСура 59. Аль-Хашр - Сбор, аяты 1-3";
            case 546:
            case 547:
            case 548:
                return "Сура 59. Аль-Хашр - Сбор";
            case 549:
            case 550:
                return "Сура 60. Аль-Мумтахина - Испытуемая";
            case 551: return "Сура 60. Аль-Мумтахина - Испытуемая, аяты 12-13, \nСура 61. Ас-Сафф - Ряд, аяты 1-5";
            case 552:
                return "Сура 61. Ас-Сафф - Ряд";
            case 553:
                return "Сура 62. Аль-Джуму'а - Пятница";
            case 554: return "Сура 62. Аль-Джуму'а - Пятница, аяты 9-11, \nСура 63. Аль-Мунафикун - Лицемеры, аяты 1-4";
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
            case 564: return "Сура 67. Аль-Мульк - Власть, аяты 27-30, \nСура 68. Аль-Калям - Письменная трость, аяты 1-15";
            case 565:
                return "Сура 68. Аль-Калям - Письменная трость";
            case 566: return "Сура 68. Аль-Калям - Письменная трость, аяты 43-52, \nСура 69. Аль-Хакка - Неизбежное, аяты 1-8";
            case 567:
                return "Сура 69. Аль-Хакка - Неизбежное";
            case 568: return "Сура 69. Аль-Хакка - Неизбежное, аяты 35-52, \nСура 70. Аль-Ма'аридж - Ступени, аяты 1-10";
            case 569:
                return "Сура 70. Аль-Ма'аридж - Ступени";
            case 570: return "Сура 70. Аль-Ма'аридж - Ступени, аяты 40-44, \nСура 71. Нух - Нух (Ной), аяты 1-10";
            case 571:
                return "Сура 71. Нух - Нух (Ной)";
            case 572:
            case 573:
                return "Сура 72. Аль-Джинн - Джинны";
            case 574:
                return "Сура 73. Аль-Муззаммиль - Закутавшийся";
            case 575: return "Сура 73. Аль-Муззаммиль - Закутавшийся, аят 20, \nСура 74. Аль-Муддассир - Завернувшийся, аяты 1-17";
            case 576:
                return "Сура 74. Аль-Муддассир - Завернувшийся";
            case 577: return "Сура 74. Аль-Муддассир - Завернувшийся, аяты 48-56, \nСура 75. Аль-Кыяма - Воскресение, аяты 1-19";
            case 578: return "Сура 75. Аль-Кыяма - Воскресение, аяты 20-30, \nСура 76. Аль-Инсан - Человек, аяты 1-5";
            case 579:
                return "Сура 76. Аль-Инсан - Человек";
            case 580: return "Сура 76. Аль-Инсан - Человек, 26-31, \nСура 77. Аль-Мурсалят - Посланные, аяты 1-19";
            case 581:
                return "Сура 77. Аль-Мурсалят - Посланные";
            case 582:
                return "Сура 78. Ан-Наба - Весть";
            case 583: return "Сура 78. Ан-Наба - Весть, аяты 31-40, \nСура 79. Ан-Назиат - Вырывающие, аяты 1-15";
            case 584:
                return "Сура 79. Ан-Назиат - Вырывающие";
            case 585:
                return "Сура 80. Абаса - Нахмурившийся";
            case 586:
                return "Сура 81. Ат-Таквир - Скручивание";
            case 587: return "Сура 82. Аль-Инфитар - Раскалывание, аяты 1-19, \n Сура 83. Аль-Мутаффифин - Обвешивающие, аяты 1-6";
            case 588: return "Сура 83. Аль-Мутаффифин - Обвешивающие, аяты 7-34";
            case 589: return "Сура 83. Аль-Мутаффифин - Обвешивающие, аят 35, \nСура 84. Аль-Иншикак - Разверзнется, аяты 1-25, Саджда-Тилява после 21-го аята";
            case 590: return "Сура 85. Аль-Бурудж - Созвездия, аяты 1-22";
            case 591: return "Сура 86. Ат-Тарик - Ночной путник, аяты 1-17, \n Сура 87. Аль-А'ля - Высочайший, аяты 1-15";
            case 592: return "Сура 87. Аль-А'ля - Высочайший, аяты 16-19, \nСура 88. Аль-Гашия - Покрывающее, аяты 1-26";
            case 593: return "Сура 89. Аль-Фаджр - Заря, аяты 1-23";
            case 594: return "Сура 89. Аль-Фаджр - Заря, аяты 24-30, \nСура 90. Аль-Баляд - Город, аяты 1-20";
            case 595: return "Сура 91. Аш-Шамс - Солнце, аяты 1-15, \nСура 92. Аль-Ляйль - Ночь, аяты 1-14";
            case 596: return "Сура 92. Аль-Ляйль - Ночь, аяты 15-21, \nСура 93. Ад-Духа - Утро, аяты 1-11, \n Сура 94. Аш-Шарх - Раскрытие, аяты 1-8";
            case 597: return "Сура 95. Ат-Тин - Инжир, аяты 1-8, \nСура 96. Аль-'Аляк - Сгусток крови, аяты 1-19, Саджда-Тилява после 19-го аята";
            case 598: return "Сура 97. Аль-Кадр - Ночь предопределения, аяты 1-5, \nСура 98. Аль-Баййина - Ясное знамение, аяты 1-7";
            case 599: return "Сура 98. Аль-Баййина - Ясное знамение, аят 8, \nСура 99. Аз-Зальзаля - Землетрясение, аяты 1-8, \nСура 100. Аль-'Адият - Скачущие, аяты 1-9";
            case 600: return "Сура 100. Аль-'Адият - Скачущие, аяты 10-11, \nСура 101. Аль-Кари'а - Великое бедствие, аяты 1-11, \nСура 102. Ат-Такасур - Страсть к приумножению, аяты 1-8";
            case 601: return "Сура 103. Аль-'Аср - Предвечернее время, аяты 1-3 \nСура 104. Аль-Хумаза - Хулитель, аяты 1-9, \nСура 105. Аль-Филь - Слон, аяты 1-5";
            case 602: return "Сура 106. Курайш - Курайшиты, аяты 1-4, \nСура 107. Аль-Ма'ун - Мелочь, аяты 1-7, \nСура 108. Аль-Кяусар - Изобилие, аяты 1-3";
            case 603: return "Сура 109. Аль-Кяфирун - Неверующие, аяты 1-6, \nСура 110. Ан-Наср - Победа, аяты 1-3, \nСура 111. Аль-Ляхаб - Пальмовые волокна, аяты 1-5";
            case 604: return "Сура 112. Аль-Ихляс - Искренность, аяты 1-4, \nСура 113. Аль-Фаляк - Рассвет, аяты 1-5, \nСура 114. Ан-Нас - Люди, аяты 1-6";

            default:
                return "Страница не найдена";
        }
    }

    public int getSuraNum(int page) {
        switch (page) {
            case 1:
                return 1;
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
                return 2;
            case 50: case 51: case 52: case 53: case 54: case 55:
            case 56: case 57: case 58: case 59: case 60:
            case 61: case 62: case 63: case 64: case 65:
            case 66: case 67: case 68: case 69: case 70:
            case 71: case 72: case 73: case 74: case 75:
            case 76:
                return 3;
            case 77: case 78: case 79: case 80: case 81:
            case 82: case 83: case 84: case 85: case 86:
            case 87: case 88: case 89: case 90: case 91:
            case 92: case 93: case 94: case 95: case 96:
            case 97: case 98: case 99: case 100: case 101:
            case 102: case 103: case 104: case 105:
                return 4;
            case 106:
            case 107: case 108:
            case 109: case 110: case 111: case 112: case 113:
            case 114: case 115: case 116: case 117: case 118:
            case 119: case 120: case 121: case 122: case 123: case 124:
            case 125: case 126: case 127:
                return 5;
            case 128: case 129: case 130: case 131: case 132:
            case 133: case 134: case 135: case 136: case 137:
            case 138: case 139: case 140: case 141: case 142:
            case 143: case 144: case 145: case 146: case 147:
            case 148: case 149: case 150:
                return 6;
            case 151: case 152: case 153: case 154:
            case 155: case 156: case 157: case 158: case 159:
            case 160: case 161: case 162: case 163: case 164:
            case 165: case 166: case 167: case 168: case 169:
            case 170: case 171: case 172: case 173: case 174:
            case 175: case 176:
                return 7;
            case 177: case 178: case 179: case 180: case 181:
            case 182: case 183: case 184: case 185: case 186:
                return 8;
            case 187: case 188: case 189: case 190: case 191:
            case 192: case 193: case 194: case 195: case 196:
            case 197: case 198: case 199: case 200: case 201:
            case 202: case 203: case 204: case 205: case 206:
            case 207:
                return 9;
            case 208: case 209: case 210: case 211:
            case 212: case 213: case 214: case 215: case 216:
            case 217: case 218: case 219: case 220:
                return 10;
            case 221: case 222: case 223: case 224:
            case 225: case 226: case 227: case 228:
            case 229: case 230: case 231: case 232:
            case 233: case 234:
                return 11;
            case 235: case 236: case 237: case 238:
            case 239: case 240: case 241: case 242:
            case 243: case 244: case 245: case 246:
            case 247: case 248:
                return 12;
            case 249: case 250: case 251: case 252:
            case 253: case 254:
                return 13;
            case 255: case 256: case 257: case 258:
            case 259: case 260: case 261:
                return 14;
            case 262:
            case 263:
            case 264:
            case 265:
            case 266:
                return 15;
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
                return 16;
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
                return 17;
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
                return 18;
            case 305:
            case 306:
            case 307:
            case 308:
            case 309:
            case 310:
            case 311:
                return 19;
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
                return 20;
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
                return 21;
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
                return 22;
            case 342:
            case 343:
            case 344:
            case 345:
            case 346:
            case 347:
            case 348:
            case 349:
                return 23;
            case 350:
            case 351:
            case 352:
            case 353:
            case 354:
            case 355:
            case 356:
            case 357:
            case 358:
                return 24;
            case 359:
            case 360:
            case 361:
            case 362:
            case 363:
            case 364:
            case 365:
            case 366:
                return 25;
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
                return 26;
            case 377:
            case 378:
            case 379:
            case 380:
            case 381:
            case 382:
            case 383:
            case 384:
                return 27;
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
                return 28;
            case 396:
            case 397:
            case 398:
            case 399:
            case 400:
            case 401:
            case 402:
            case 403:
                return 29;
            case 404:
            case 405:
            case 406:
            case 407:
            case 408:
            case 409:
            case 410:
                return 30;
            case 411:
            case 412:
            case 413:
            case 414:
                return 31;
            case 415:
            case 416:
            case 417:
                return 32;
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
                return 33;
            case 428:
            case 429:
            case 430:
            case 431:
            case 432:
            case 433:
                return 34;
            case 434:
            case 435:
            case 436:
            case 437:
            case 438:
            case 439:
                return 35;
            case 440:
            case 441:
            case 442:
            case 443:
            case 444:
            case 445:
                return 36;
            case 446:
            case 447:
            case 448:
            case 449:
            case 450:
            case 451:
            case 452:
                return 37;
            case 453:
            case 454:
            case 455:
            case 456:
            case 457:
                return 38;
            case 458:
            case 459:
            case 460:
            case 461:
            case 462:
            case 463:
            case 464:
            case 465:
            case 466:
                return 39;
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
                return 40;
            case 477:
            case 478:
            case 479:
            case 480:
            case 481:
            case 482:
                return 41;
            case 483:
            case 484:
            case 485:
            case 486:
            case 487:
            case 488:
                return 42;
            case 489:
            case 490:
            case 491:
            case 492:
            case 493:
            case 494:
            case 495:
                return 43;
            case 496:
            case 497:
            case 498:
                return 44;
            case 499:
            case 500:
            case 501:
                return 45;
            case 502:
            case 503:
            case 504:
            case 505:
            case 506:
                return 46;
            case 507:
            case 508:
            case 509:
            case 510:
                return 47;
            case 511:
            case 512:
            case 513:
            case 514:
                return 48;
            case 515:
            case 516:
            case 517:
                return 49;
            case 518:
            case 519:
                return 50;
            case 520:
            case 521:
            case 522:
                return 51;
            case 523:
            case 524:
            case 525:
                return 52;
            case 526:
            case 527:
                return 53;
            case 528:
            case 529:
            case 530:
                return 54;
            case 531:
            case 532:
            case 533:
                return 55;
            case 534:
            case 535:
            case 536:
                return 56;
            case 537:
            case 538:
            case 539:
            case 540:
            case 541:
                return 57;
            case 542:
            case 543:
            case 544:
                return 58;
            case 545:
            case 546:
            case 547:
            case 548:
                return 59;
            case 549:
            case 550:
                return 60;
            case 551:
            case 552:
                return 61;
            case 553:
                return 62;
            case 554:
            case 555:
                return 63;
            case 556:
            case 557:
                return 64;
            case 558:
            case 559:
                return 65;
            case 560:
            case 561:
                return 66;
            case 562:
            case 563:
                return 67;
            case 564:
            case 565:
                return 58;
            case 566:
            case 567:
                return 69;
            case 568:
            case 569:
                return 70;
            case 570:
            case 571:
                return 71;
            case 572:
            case 573:
                return 72;
            case 574:
                return 73;
            case 575:
            case 576:
                return 74;
            case 577: return 75;
            case 578:
            case 579:
                return 76;
            case 580:
            case 581:
                return 77;
            case 582:
                return 78;
            case 583:
            case 584:
                return 79;
            case 585:
                return 80;
            case 586:
                return 81;
            case 587: return 82;
            case 588: return 83;
            case 589: return 84;
            case 590: return 85;
            case 591: return 86;
            case 592: return 88;
            case 593: return 89;
            case 594: return 90;
            case 595: return 91;
            case 596: return 93;
            case 597: return 95;
            case 598: return 97;
            case 599: return 99;
            case 600: return 101;
            case 601: return 103;
            case 602: return 106;
            case 603: return 109;
            case 604: return 112;

            default:
                return getSuraNum(getCurrentPosition());
        }
    }

    public int getNumAyatsOfSure(int sure) {
        switch (sure) {
            case 1: return 7;
            case 2: return 286;
            case 3: return 200;
            case 4: return 176;
            case 5: return 120;
            case 6: return 165;
            case 7: return 206;
            case 8: return 75;
            case 9: return 129;
            case 10: return 109;
            case 11: return 123;
            case 12: return 111;
            case 13: return 43;
            case 14: return 52;
            case 15: return 99;
            case 16: return 128;
            case 17: return 111;
            case 18: return 110;
            case 19: return 98;
            case 20: return 135;
            case 21: return 112;
            case 22: return 78;
            case 23: return 118;
            case 24: return 64;
            case 25: return 77;
            case 26: return 227;
            case 27: return 93;
            case 28: return 88;
            case 29: return 69;
            case 30: return 60;
            case 31: return 34;
            case 32: return 30;
            case 33: return 73;
            case 34: return 54;
            case 35: return 45;
            case 36: return 83;
            case 37: return 182;
            case 38: return 88;
            case 39: return 75;
            case 40: return 85;
            case 41: return 54;
            case 42: return 53;
            case 43: return 89;
            case 44: return 59;
            case 45: return 37;
            case 46: return 35;
            case 47: return 38;
            case 48: return 29;
            case 49: return 18;
            case 50: return 45;
            case 51: return 60;
            case 52: return 49;
            case 53: return 62;
            case 54: return 55;
            case 55: return 78;
            case 56: return 96;
            case 57: return 29;
            case 58: return 22;
            case 59: return 24;
            case 60: return 13;
            case 61: return 14;
            case 62: return 11;
            case 63: return 11;
            case 64: return 18;
            case 65: return 12;
            case 66: return 12;
            case 67: return 30;
            case 68: return 52;
            case 69: return 52;
            case 70: return 44;
            case 71: return 28;
            case 72: return 28;
            case 73: return 20;
            case 74: return 56;
            case 75: return 40;
            case 76: return 31;
            case 77: return 50;
            case 78: return 40;
            case 79: return 46;
            case 80: return 42;
            case 81: return 29;
            case 82: return 19;
            case 83: return 36;
            case 84: return 25;
            case 85: return 22;
            case 86: return 17;
            case 87: return 19;
            case 88: return 26;
            case 89: return 30;
            case 90: return 20;
            case 91: return 15;
            case 92: return 21;
            case 93: return 11;
            case 94: return 8;
            case 95: return 8;
            case 96: return 19;
            case 97: return 5;
            case 98: return 8;
            case 99: return 8;
            case 100: return 11;
            case 101: return 11;
            case 102: return 8;
            case 103: return 3;
            case 104: return 9;
            case 105: return 5;
            case 106: return 4;
            case 107: return 7;
            case 108: return 3;
            case 109: return 6;
            case 110: return 3;
            case 111: return 5;
            case 112: return 4;
            case 113: return 5;
            case 114: return 6;

            default: return getNumAyatsOfSure(getSuraNum(getCurrentPosition()));
        }

    }

    //TODO
    public int goToAyat(int numSure, int numAyat) {
        int numPage = 1;
        switch (numSure) {
            case 1:
                if (numAyat >= 1 && numAyat <= 7) numPage = 1;
                else return -1;
                break;
            case 2:
                if (numAyat >= 1 && numAyat <= 5) numPage = 2;
                else if (numAyat >= 6 && numAyat <= 16) numPage = 3;
                else if (numAyat >= 17 && numAyat <= 24) numPage = 4;
                else if (numAyat >= 25 && numAyat <= 29) numPage = 5;
                else if (numAyat >= 30 && numAyat <= 37) numPage = 6;
                else if (numAyat >= 38 && numAyat <= 48) numPage = 7;
                else if (numAyat >= 49 && numAyat <= 57) numPage = 8;
                else if (numAyat >= 58 && numAyat <= 61) numPage = 9;
                else if (numAyat >= 62 && numAyat <= 69) numPage = 10;
                else if (numAyat >= 70 && numAyat <= 76) numPage = 11;
                else if (numAyat >= 77 && numAyat <= 83) numPage = 12;
                else if (numAyat >= 84 && numAyat <= 88) numPage = 13;
                else if (numAyat >= 89 && numAyat <= 93) numPage = 14;
                else if (numAyat >= 94 && numAyat <= 101) numPage = 15;
                else if (numAyat >= 102 && numAyat <= 105) numPage = 16;
                else if (numAyat >= 106 && numAyat <= 112) numPage = 17;
                else if (numAyat >= 113 && numAyat <= 119) numPage = 18;
                else if (numAyat >= 120 && numAyat <= 126) numPage = 19;
                else if (numAyat >= 127 && numAyat <= 134) numPage = 20;
                else if (numAyat >= 135 && numAyat <= 141) numPage = 21;
                else if (numAyat >= 142 && numAyat <= 145) numPage = 22;
                else if (numAyat >= 146 && numAyat <= 153) numPage = 23;
                else if (numAyat >= 154 && numAyat <= 163) numPage = 24;
                else if (numAyat >= 164 && numAyat <= 169) numPage = 25;
                else if (numAyat >= 170 && numAyat <= 176) numPage = 26;
                else if (numAyat >= 177 && numAyat <= 181) numPage = 27;
                else if (numAyat >= 182 && numAyat <= 186) numPage = 28;
                else if (numAyat >= 187 && numAyat <= 190) numPage = 29;
                else if (numAyat >= 191 && numAyat <= 196) numPage = 30;
                else if (numAyat >= 197 && numAyat <= 202) numPage = 31;
                else if (numAyat >= 203 && numAyat <= 210) numPage = 32;
                else if (numAyat >= 211 && numAyat <= 215) numPage = 33;
                else if (numAyat >= 216 && numAyat <= 219) numPage = 34;
                else if (numAyat >= 220 && numAyat <= 224) numPage = 35;
                else if (numAyat >= 225 && numAyat <= 230) numPage = 36;
                else if (numAyat >= 231 && numAyat <= 233) numPage = 37;
                else if (numAyat >= 234 && numAyat <= 237) numPage = 38;
                else if (numAyat >= 238 && numAyat <= 245) numPage = 39;
                else if (numAyat >= 246 && numAyat <= 248) numPage = 40;
                else if (numAyat >= 249 && numAyat <= 252) numPage = 41;
                else if (numAyat >= 253 && numAyat <= 256) numPage = 42;
                else if (numAyat >= 257 && numAyat <= 259) numPage = 43;
                else if (numAyat >= 260 && numAyat <= 264) numPage = 44;
                else if (numAyat >= 265 && numAyat <= 269) numPage = 45;
                else if (numAyat >= 270 && numAyat <= 274) numPage = 46;
                else if (numAyat >= 275 && numAyat <= 281) numPage = 47;
                else if (numAyat == 282) numPage = 48;
                else if (numAyat >= 283 && numAyat <= 286) numPage = 49;
                break;
            case 3:
                if (numAyat >= 1 && numAyat <= 9) numPage = 50;
                else if (numAyat >= 10 && numAyat <= 15) numPage = 51;
                else if (numAyat >= 16 && numAyat <= 22) numPage = 52;
                else if (numAyat >= 23 && numAyat <= 29) numPage = 53;
                else if (numAyat >= 30 && numAyat <= 37) numPage = 54;
                else if (numAyat >= 38 && numAyat <= 45) numPage = 55;
                else if (numAyat >= 46 && numAyat <= 52) numPage = 56;
                else if (numAyat >= 53 && numAyat <= 61) numPage = 57;
                else if (numAyat >= 62 && numAyat <= 70) numPage = 58;
                else if (numAyat >= 71 && numAyat <= 77) numPage = 59;
                else if (numAyat >= 78 && numAyat <= 83) numPage = 60;
                else if (numAyat >= 84 && numAyat <= 91) numPage = 61;
                else if (numAyat >= 92 && numAyat <= 100) numPage = 62;
                else if (numAyat >= 101 && numAyat <= 108) numPage = 63;
                else if (numAyat >= 109 && numAyat <= 115) numPage = 64;
                else if (numAyat >= 116 && numAyat <= 121) numPage = 65;
                else if (numAyat >= 122 && numAyat <= 132) numPage = 66;
                else if (numAyat >= 133 && numAyat <= 140) numPage = 67;
                else if (numAyat >= 141 && numAyat <= 148) numPage = 68;
                else if (numAyat >= 149 && numAyat <= 153) numPage = 69;
                else if (numAyat >= 154 && numAyat <= 157) numPage = 70;
                else if (numAyat >= 158 && numAyat <= 165) numPage = 71;
                else if (numAyat >= 166 && numAyat <= 173) numPage = 72;
                else if (numAyat >= 174 && numAyat <= 180) numPage = 73;
                else if (numAyat >= 181 && numAyat <= 186) numPage = 74;
                else if (numAyat >= 187 && numAyat <= 194) numPage = 75;
                else if (numAyat >= 195 && numAyat <= 200) numPage = 76;
                break;

            case 4:
                if (numAyat >= 1 && numAyat <= 6) numPage = 77;
                else if (numAyat >= 7 && numAyat <= 11) numPage = 78;
                else if (numAyat >= 12 && numAyat <= 14) numPage = 79;
                else if (numAyat >= 15 && numAyat <= 19) numPage = 80;
                else if (numAyat >= 20 && numAyat <= 23) numPage = 81;
                else if (numAyat >= 24 && numAyat <= 26) numPage = 82;
                else if (numAyat >= 27 && numAyat <= 33) numPage = 83;
                else if (numAyat >= 34 && numAyat <= 37) numPage = 84;
                else if (numAyat >= 38 && numAyat <= 44) numPage = 85;
                else if (numAyat >= 45 && numAyat <= 51) numPage = 86;
                else if (numAyat >= 52 && numAyat <= 59) numPage = 87;
                else if (numAyat >= 60 && numAyat <= 65) numPage = 88;
                else if (numAyat >= 66 && numAyat <= 74) numPage = 89;
                else if (numAyat >= 75 && numAyat <= 79) numPage = 90;
                else if (numAyat >= 80 && numAyat <= 86) numPage = 91;
                else if (numAyat >= 87 && numAyat <= 91) numPage = 92;
                else if (numAyat >= 92 && numAyat <= 94) numPage = 93;
                else if (numAyat >= 95 && numAyat <= 101) numPage = 94;
                else if (numAyat >= 102 && numAyat <= 105) numPage = 95;
                else if (numAyat >= 106 && numAyat <= 113) numPage = 96;
                else if (numAyat >= 114 && numAyat <= 121) numPage = 97;
                else if (numAyat >= 122 && numAyat <= 127) numPage = 98;
                else if (numAyat >= 128 && numAyat <= 134) numPage = 99;
                else if (numAyat >= 135 && numAyat <= 140) numPage = 100;
                else if (numAyat >= 141 && numAyat <= 147) numPage = 101;
                else if (numAyat >= 148 && numAyat <= 153) numPage = 102;
                else if (numAyat >= 154 && numAyat <= 162) numPage = 103;
                else if (numAyat >= 163 && numAyat <= 170) numPage = 104;
                else if (numAyat >= 171 && numAyat <= 175) numPage = 105;
                else if (numAyat == 176) numPage = 106;
                break;

            case 5:
                if (numAyat >= 1 && numAyat <= 2) numPage = 106;
                else if (numAyat >= 3 && numAyat <= 5) numPage = 107;
                else if (numAyat >= 6 && numAyat <= 9) numPage = 108;
                else if (numAyat >= 10 && numAyat <= 13) numPage = 109;
                else if (numAyat >= 14 && numAyat <= 17) numPage = 110;
                else if (numAyat >= 18 && numAyat <= 23) numPage = 111;
                else if (numAyat >= 24 && numAyat <= 31) numPage = 112;
                else if (numAyat >= 32 && numAyat <= 36) numPage = 113;
                else if (numAyat >= 37 && numAyat <= 41) numPage = 114;
                else if (numAyat >= 42 && numAyat <= 45) numPage = 115;
                else if (numAyat >= 46 && numAyat <= 50) numPage = 116;
                else if (numAyat >= 51 && numAyat <= 57) numPage = 117;
                else if (numAyat >= 58 && numAyat <= 64) numPage = 118;
                else if (numAyat >= 65 && numAyat <= 70) numPage = 119;
                else if (numAyat >= 71 && numAyat <= 76) numPage = 120;
                else if (numAyat >= 77 && numAyat <= 82) numPage = 121;
                else if (numAyat >= 83 && numAyat <= 89) numPage = 122;
                else if (numAyat >= 90 && numAyat <= 95) numPage = 123;
                else if (numAyat >= 96 && numAyat <= 103) numPage = 124;
                else if (numAyat >= 104 && numAyat <= 108) numPage = 125;
                else if (numAyat >= 109 && numAyat <= 113) numPage = 126;
                else if (numAyat >= 114 && numAyat <= 120) numPage = 127;
                break;

            case 6:
                if (numAyat >= 1 && numAyat <= 8) numPage = 128;
                else if (numAyat >= 9 && numAyat <= 18) numPage = 129;
                else if (numAyat >= 19 && numAyat <= 27) numPage = 130;
                else if (numAyat >= 28 && numAyat <= 35) numPage = 131;
                else if (numAyat >= 36 && numAyat <= 44) numPage = 132;
                else if (numAyat >= 45 && numAyat <= 52) numPage = 133;
                else if (numAyat >= 53 && numAyat <= 59) numPage = 134;
                else if (numAyat >= 60 && numAyat <= 68) numPage = 135;
                else if (numAyat >= 69 && numAyat <= 73) numPage = 136;
                else if (numAyat >= 74 && numAyat <= 81) numPage = 137;
                else if (numAyat >= 82 && numAyat <= 90) numPage = 138;
                else if (numAyat >= 91 && numAyat <= 94) numPage = 139;
                else if (numAyat >= 95 && numAyat <= 101) numPage = 140;
                else if (numAyat >= 102 && numAyat <= 110) numPage = 141;
                else if (numAyat >= 111 && numAyat <= 118) numPage = 142;
                else if (numAyat >= 119 && numAyat <= 124) numPage = 143;
                else if (numAyat >= 125 && numAyat <= 131) numPage = 144;
                else if (numAyat >= 132 && numAyat <= 137) numPage = 145;
                else if (numAyat >= 138 && numAyat <= 142) numPage = 146;
                else if (numAyat >= 143 && numAyat <= 146) numPage = 147;
                else if (numAyat >= 147 && numAyat <= 151) numPage = 148;
                else if (numAyat >= 152 && numAyat <= 157) numPage = 149;
                else if (numAyat >= 158 && numAyat <= 165) numPage = 150;
                break;

            case 7:
                if (numAyat >= 1 && numAyat <= 11) numPage = 151;
                else if (numAyat >= 12 && numAyat <= 22) numPage = 152;
                else if (numAyat >= 23 && numAyat <= 30) numPage = 153;
                else if (numAyat >= 31 && numAyat <= 37) numPage = 154;
                else if (numAyat >= 38 && numAyat <= 43) numPage = 155;
                else if (numAyat >= 44 && numAyat <= 51) numPage = 156;
                else if (numAyat >= 52 && numAyat <= 57) numPage = 157;
                else if (numAyat >= 58 && numAyat <= 67) numPage = 158;
                else if (numAyat >= 68 && numAyat <= 73) numPage = 159;
                else if (numAyat >= 74 && numAyat <= 81) numPage = 160;
                else if (numAyat >= 82 && numAyat <= 87) numPage = 161;
                else if (numAyat >= 88 && numAyat <= 95) numPage = 162;
                else if (numAyat >= 96 && numAyat <= 104) numPage = 163;
                else if (numAyat >= 105 && numAyat <= 120) numPage = 164;
                else if (numAyat >= 121 && numAyat <= 130) numPage = 165;
                else if (numAyat >= 131 && numAyat <= 137) numPage = 166;
                else if (numAyat >= 138 && numAyat <= 143) numPage = 167;
                else if (numAyat >= 144 && numAyat <= 149) numPage = 168;
                else if (numAyat >= 150 && numAyat <= 155) numPage = 169;
                else if (numAyat >= 156 && numAyat <= 159) numPage = 170;
                else if (numAyat >= 160 && numAyat <= 163) numPage = 171;
                else if (numAyat >= 164 && numAyat <= 170) numPage = 172;
                else if (numAyat >= 171 && numAyat <= 178) numPage = 173;
                else if (numAyat >= 179 && numAyat <= 187) numPage = 174;
                else if (numAyat >= 188 && numAyat <= 195) numPage = 175;
                else if (numAyat >= 196 && numAyat <= 206) numPage = 176;
                break;

            case 8:
                if (numAyat >= 1 && numAyat <= 8) numPage = 177;
                else if (numAyat >= 9 && numAyat <= 16) numPage = 178;
                else if (numAyat >= 17 && numAyat <= 25) numPage = 179;
                else if (numAyat >= 26 && numAyat <= 33) numPage = 180;
                else if (numAyat >= 34 && numAyat <= 40) numPage = 181;
                else if (numAyat >= 41 && numAyat <= 45) numPage = 182;
                else if (numAyat >= 46 && numAyat <= 52) numPage = 183;
                else if (numAyat >= 53 && numAyat <= 61) numPage = 184;
                else if (numAyat >= 62 && numAyat <= 69) numPage = 185;
                else if (numAyat >= 70 && numAyat <= 75) numPage = 186;
                break;

            case 9:
                if (numAyat >= 1 && numAyat <= 6) numPage = 187;
                else if (numAyat >= 7 && numAyat <= 13) numPage = 188;
                else if (numAyat >= 14 && numAyat <= 20) numPage = 189;
                else if (numAyat >= 21 && numAyat <= 26) numPage = 190;
                else if (numAyat >= 27 && numAyat <= 31) numPage = 191;
                else if (numAyat >= 32 && numAyat <= 36) numPage = 192;
                else if (numAyat >= 37 && numAyat <= 40) numPage = 193;
                else if (numAyat >= 41 && numAyat <= 47) numPage = 194;
                else if (numAyat >= 48 && numAyat <= 53) numPage = 195;
                else if (numAyat >= 54 && numAyat <= 61) numPage = 196;
                else if (numAyat >= 62 && numAyat <= 68) numPage = 197;
                else if (numAyat >= 69 && numAyat <= 72) numPage = 198;
                else if (numAyat >= 73 && numAyat <= 79) numPage = 199;
                else if (numAyat >= 80 && numAyat <= 86) numPage = 200;
                else if (numAyat >= 87 && numAyat <= 93) numPage = 201;
                else if (numAyat >= 94 && numAyat <= 99) numPage = 202;
                else if (numAyat >= 100 && numAyat <= 106) numPage = 203;
                else if (numAyat >= 107 && numAyat <= 111) numPage = 204;
                else if (numAyat >= 112 && numAyat <= 117) numPage = 205;
                else if (numAyat >= 118 && numAyat <= 122) numPage = 206;
                else if (numAyat >= 123 && numAyat <= 129) numPage = 207;
                break;

            case 10:
                if (numAyat >= 1 && numAyat <= 6) numPage = 208;
                else if (numAyat >= 7 && numAyat <= 14) numPage = 209;
                else if (numAyat >= 15 && numAyat <= 20) numPage = 210;
                else if (numAyat >= 21 && numAyat <= 25) numPage = 211;
                else if (numAyat >= 26 && numAyat <= 33) numPage = 212;
                else if (numAyat >= 34 && numAyat <= 42) numPage = 213;
                else if (numAyat >= 43 && numAyat <= 53) numPage = 214;
                else if (numAyat >= 54 && numAyat <= 61) numPage = 215;
                else if (numAyat >= 62 && numAyat <= 70) numPage = 216;
                else if (numAyat >= 71 && numAyat <= 78) numPage = 217;
                else if (numAyat >= 79 && numAyat <= 88) numPage = 218;
                else if (numAyat >= 89 && numAyat <= 97) numPage = 219;
                else if (numAyat >= 98 && numAyat <= 106) numPage = 220;
                else if (numAyat >= 107 && numAyat <= 109) numPage = 221;
                break;

            case 11:
                if (numAyat >= 1 && numAyat <= 5) numPage = 221;
                else if (numAyat >= 6 && numAyat <= 12) numPage = 222;
                else if (numAyat >= 13 && numAyat <= 19) numPage = 223;
                else if (numAyat >= 20 && numAyat <= 28) numPage = 224;
                else if (numAyat >= 29 && numAyat <= 37) numPage = 225;
                else if (numAyat >= 38 && numAyat <= 45) numPage = 226;
                else if (numAyat >= 46 && numAyat <= 53) numPage = 227;
                else if (numAyat >= 54 && numAyat <= 62) numPage = 228;
                else if (numAyat >= 63 && numAyat <= 71) numPage = 229;
                else if (numAyat >= 72 && numAyat <= 81) numPage = 230;
                else if (numAyat >= 82 && numAyat <= 88) numPage = 231;
                else if (numAyat >= 89 && numAyat <= 97) numPage = 232;
                else if (numAyat >= 98 && numAyat <= 108) numPage = 233;
                else if (numAyat >= 109 && numAyat <= 117) numPage = 234;
                else if (numAyat >= 118 && numAyat <= 123) numPage = 235;
                break;

            case 12:
                if (numAyat >= 1 && numAyat <= 4) numPage = 235;
                else if (numAyat >= 5 && numAyat <= 14) numPage = 236;
                else if (numAyat >= 15 && numAyat <= 22) numPage = 237;
                else if (numAyat >= 23 && numAyat <= 30) numPage = 238;
                else if (numAyat >= 31 && numAyat <= 37) numPage = 239;
                else if (numAyat >= 38 && numAyat <= 43) numPage = 240;
                else if (numAyat >= 44 && numAyat <= 52) numPage = 241;
                else if (numAyat >= 53 && numAyat <= 63) numPage = 242;
                else if (numAyat >= 64 && numAyat <= 69) numPage = 243;
                else if (numAyat >= 70 && numAyat <= 78) numPage = 244;
                else if (numAyat >= 79 && numAyat <= 86) numPage = 245;
                else if (numAyat >= 87 && numAyat <= 95) numPage = 246;
                else if (numAyat >= 96 && numAyat <= 103) numPage = 247;
                else if (numAyat >= 104 && numAyat <= 111) numPage = 248;
                break;

            case 13:
                if (numAyat >= 1 && numAyat <= 5) numPage = 249;
                else if (numAyat >= 6 && numAyat <= 13) numPage = 250;
                else if (numAyat >= 14 && numAyat <= 18) numPage = 251;
                else if (numAyat >= 19 && numAyat <= 28) numPage = 252;
                else if (numAyat >= 29 && numAyat <= 34) numPage = 253;
                else if (numAyat >= 35 && numAyat <= 42) numPage = 254;
                else if (numAyat == 43) numPage = 255; // отдельный аят
                break;

            case 14:
                if (numAyat >= 1 && numAyat <= 5) numPage = 255;
                else if (numAyat >= 6 && numAyat <= 10) numPage = 256;
                else if (numAyat >= 11 && numAyat <= 18) numPage = 257;
                else if (numAyat >= 19 && numAyat <= 24) numPage = 258;
                else if (numAyat >= 25 && numAyat <= 33) numPage = 259;
                else if (numAyat >= 34 && numAyat <= 42) numPage = 260;
                else if (numAyat >= 43 && numAyat <= 52) numPage = 261;
                break;

            case 15:
                if (numAyat >= 1 && numAyat <= 15) numPage = 262;
                else if (numAyat >= 16 && numAyat <= 31) numPage = 263;
                else if (numAyat >= 32 && numAyat <= 51) numPage = 264;
                else if (numAyat >= 52 && numAyat <= 70) numPage = 265;
                else if (numAyat >= 71 && numAyat <= 90) numPage = 266;
                else if (numAyat >= 91 && numAyat <= 99) numPage = 267;
                break;

            case 16:
                if (numAyat >= 1 && numAyat <= 6) numPage = 267;
                else if (numAyat >= 7 && numAyat <= 14) numPage = 268;
                else if (numAyat >= 15 && numAyat <= 26) numPage = 269;
                else if (numAyat >= 27 && numAyat <= 34) numPage = 270;
                else if (numAyat >= 35 && numAyat <= 42) numPage = 271;
                else if (numAyat >= 43 && numAyat <= 54) numPage = 272;
                else if (numAyat >= 55 && numAyat <= 64) numPage = 273;
                else if (numAyat >= 65 && numAyat <= 72) numPage = 274;
                else if (numAyat >= 73 && numAyat <= 79) numPage = 275;
                else if (numAyat >= 80 && numAyat <= 87) numPage = 276;
                else if (numAyat >= 88 && numAyat <= 93) numPage = 277;
                else if (numAyat >= 94 && numAyat <= 102) numPage = 278;
                else if (numAyat >= 103 && numAyat <= 110) numPage = 279;
                else if (numAyat >= 111 && numAyat <= 118) numPage = 280;
                else if (numAyat >= 119 && numAyat <= 128) numPage = 281;
                break;

            case 17:
                if (numAyat >= 1 && numAyat <= 7) numPage = 282;
                else if (numAyat >= 8 && numAyat <= 17) numPage = 283;
                else if (numAyat >= 18 && numAyat <= 27) numPage = 284;
                else if (numAyat >= 28 && numAyat <= 38) numPage = 285;
                else if (numAyat >= 39 && numAyat <= 49) numPage = 286;
                else if (numAyat >= 50 && numAyat <= 58) numPage = 287;
                else if (numAyat >= 59 && numAyat <= 66) numPage = 288;
                else if (numAyat >= 67 && numAyat <= 75) numPage = 289;
                else if (numAyat >= 76 && numAyat <= 86) numPage = 290;
                else if (numAyat >= 87 && numAyat <= 96) numPage = 291;
                else if (numAyat >= 97 && numAyat <= 104) numPage = 292;
                else if (numAyat >= 105 && numAyat <= 111) numPage = 293;
                break;

            case 18:
                if (numAyat >= 1 && numAyat <= 4) numPage = 293;
                else if (numAyat >= 5 && numAyat <= 15) numPage = 294;
                else if (numAyat >= 16 && numAyat <= 20) numPage = 295;
                else if (numAyat >= 21 && numAyat <= 27) numPage = 296;
                else if (numAyat >= 28 && numAyat <= 34) numPage = 297;
                else if (numAyat >= 35 && numAyat <= 45) numPage = 298;
                else if (numAyat >= 46 && numAyat <= 53) numPage = 299;
                else if (numAyat >= 54 && numAyat <= 61) numPage = 300;
                else if (numAyat >= 62 && numAyat <= 74) numPage = 301;
                else if (numAyat >= 75 && numAyat <= 83) numPage = 302;
                else if (numAyat >= 84 && numAyat <= 97) numPage = 303;
                else if (numAyat >= 98 && numAyat <= 110) numPage = 304;
                break;

            case 19:
                if (numAyat >= 1 && numAyat <= 11) numPage = 305;
                else if (numAyat >= 12 && numAyat <= 25) numPage = 306;
                else if (numAyat >= 26 && numAyat <= 38) numPage = 307;
                else if (numAyat >= 39 && numAyat <= 51) numPage = 308;
                else if (numAyat >= 52 && numAyat <= 64) numPage = 309;
                else if (numAyat >= 65 && numAyat <= 76) numPage = 310;
                else if (numAyat >= 77 && numAyat <= 95) numPage = 311;
                else if (numAyat >= 96 && numAyat <= 98) numPage = 312;
                break;

            case 20:
                if (numAyat >= 1 && numAyat <= 12) numPage = 312;
                else if (numAyat >= 13 && numAyat <= 37) numPage = 313;
                else if (numAyat >= 38 && numAyat <= 51) numPage = 314;
                else if (numAyat >= 52 && numAyat <= 64) numPage = 315;
                else if (numAyat >= 65 && numAyat <= 76) numPage = 316;
                else if (numAyat >= 77 && numAyat <= 87) numPage = 317;
                else if (numAyat >= 88 && numAyat <= 98) numPage = 318;
                else if (numAyat >= 99 && numAyat <= 113) numPage = 319;
                else if (numAyat >= 114 && numAyat <= 125) numPage = 320;
                else if (numAyat >= 126 && numAyat <= 135) numPage = 321;
                break;

            case 21:
                if (numAyat >= 1 && numAyat <= 10) numPage = 322;
                else if (numAyat >= 11 && numAyat <= 24) numPage = 323;
                else if (numAyat >= 25 && numAyat <= 35) numPage = 324;
                else if (numAyat >= 36 && numAyat <= 44) numPage = 325;
                else if (numAyat >= 45 && numAyat <= 57) numPage = 326;
                else if (numAyat >= 58 && numAyat <= 72) numPage = 327;
                else if (numAyat >= 73 && numAyat <= 81) numPage = 328;
                else if (numAyat >= 82 && numAyat <= 90) numPage = 329;
                else if (numAyat >= 91 && numAyat <= 101) numPage = 330;
                else if (numAyat >= 102 && numAyat <= 112) numPage = 331;
                break;

            case 22:
                if (numAyat >= 1 && numAyat <= 5) numPage = 332;
                else if (numAyat >= 6 && numAyat <= 15) numPage = 333;
                else if (numAyat >= 16 && numAyat <= 23) numPage = 334;
                else if (numAyat >= 24 && numAyat <= 30) numPage = 335;
                else if (numAyat >= 31 && numAyat <= 38) numPage = 336;
                else if (numAyat >= 39 && numAyat <= 46) numPage = 337;
                else if (numAyat >= 47 && numAyat <= 55) numPage = 338;
                else if (numAyat >= 56 && numAyat <= 64) numPage = 339;
                else if (numAyat >= 65 && numAyat <= 72) numPage = 340;
                else if (numAyat >= 73 && numAyat <= 78) numPage = 341;
                break;

            case 23:
                if (numAyat >= 1 && numAyat <= 17) numPage = 342;
                else if (numAyat >= 18 && numAyat <= 27) numPage = 343;
                else if (numAyat >= 28 && numAyat <= 42) numPage = 344;
                else if (numAyat >= 43 && numAyat <= 59) numPage = 345;
                else if (numAyat >= 60 && numAyat <= 74) numPage = 346;
                else if (numAyat >= 75 && numAyat <= 89) numPage = 347;
                else if (numAyat >= 90 && numAyat <= 104) numPage = 348;
                else if (numAyat >= 105 && numAyat <= 118) numPage = 349;
                break;

            case 24:
                if (numAyat >= 1 && numAyat <= 10) numPage = 350;
                else if (numAyat >= 11 && numAyat <= 20) numPage = 351;
                else if (numAyat >= 21 && numAyat <= 27) numPage = 352;
                else if (numAyat >= 28 && numAyat <= 31) numPage = 353;
                else if (numAyat >= 32 && numAyat <= 36) numPage = 354;
                else if (numAyat >= 37 && numAyat <= 43) numPage = 355;
                else if (numAyat >= 44 && numAyat <= 53) numPage = 356;
                else if (numAyat >= 54 && numAyat <= 58) numPage = 357;
                else if (numAyat >= 59 && numAyat <= 61) numPage = 358;
                else if (numAyat >= 62 && numAyat <= 64) numPage = 359;
                break;

            case 25:
                if (numAyat >= 1 && numAyat <= 2) numPage = 359;
                else if (numAyat >= 3 && numAyat <= 11) numPage = 360;
                else if (numAyat >= 12 && numAyat <= 20) numPage = 361;
                else if (numAyat >= 21 && numAyat <= 32) numPage = 362;
                else if (numAyat >= 33 && numAyat <= 43) numPage = 363;
                else if (numAyat >= 44 && numAyat <= 55) numPage = 364;
                else if (numAyat >= 56 && numAyat <= 67) numPage = 365;
                else if (numAyat >= 68 && numAyat <= 77) numPage = 366;
                break;

            case 26:
                if (numAyat >= 1 && numAyat <= 19) numPage = 367;
                else if (numAyat >= 20 && numAyat <= 39) numPage = 368;
                else if (numAyat >= 40 && numAyat <= 60) numPage = 369;
                else if (numAyat >= 61 && numAyat <= 83) numPage = 370;
                else if (numAyat >= 84 && numAyat <= 111) numPage = 371;
                else if (numAyat >= 112 && numAyat <= 136) numPage = 372;
                else if (numAyat >= 137 && numAyat <= 159) numPage = 373;
                else if (numAyat >= 160 && numAyat <= 183) numPage = 374;
                else if (numAyat >= 184 && numAyat <= 206) numPage = 375;
                else if (numAyat >= 207 && numAyat <= 227) numPage = 376;
                break;

            case 27:
                if (numAyat >= 1 && numAyat <= 13) numPage = 377;
                else if (numAyat >= 14 && numAyat <= 22) numPage = 378;
                else if (numAyat >= 23 && numAyat <= 35) numPage = 379;
                else if (numAyat >= 36 && numAyat <= 44) numPage = 380;
                else if (numAyat >= 45 && numAyat <= 55) numPage = 381;
                else if (numAyat >= 56 && numAyat <= 63) numPage = 382;
                else if (numAyat >= 64 && numAyat <= 76) numPage = 383;
                else if (numAyat >= 77 && numAyat <= 88) numPage = 384;
                else if (numAyat >= 89 && numAyat <= 93) numPage = 385;
                break;

            case 28:
                if (numAyat >= 1 && numAyat <= 5) numPage = 385;
                else if (numAyat >= 6 && numAyat <= 13) numPage = 386;
                else if (numAyat >= 14 && numAyat <= 21) numPage = 387;
                else if (numAyat >= 22 && numAyat <= 28) numPage = 388;
                else if (numAyat >= 29 && numAyat <= 35) numPage = 389;
                else if (numAyat >= 36 && numAyat <= 43) numPage = 390;
                else if (numAyat >= 44 && numAyat <= 50) numPage = 391;
                else if (numAyat >= 51 && numAyat <= 59) numPage = 392;
                else if (numAyat >= 60 && numAyat <= 70) numPage = 393;
                else if (numAyat >= 71 && numAyat <= 77) numPage = 394;
                else if (numAyat >= 78 && numAyat <= 84) numPage = 395;
                else if (numAyat >= 85 && numAyat <= 88) numPage = 396;
                break;

            case 29:
                if (numAyat >= 1 && numAyat <= 6) numPage = 396;
                else if (numAyat >= 7 && numAyat <= 14) numPage = 397;
                else if (numAyat >= 15 && numAyat <= 23) numPage = 398;
                else if (numAyat >= 24 && numAyat <= 30) numPage = 399;
                else if (numAyat >= 31 && numAyat <= 38) numPage = 400;
                else if (numAyat >= 39 && numAyat <= 45) numPage = 401;
                else if (numAyat >= 46 && numAyat <= 52) numPage = 402;
                else if (numAyat >= 53 && numAyat <= 63) numPage = 403;
                else if (numAyat >= 64 && numAyat <= 69) numPage = 404;
                break;

            case 30:
                if (numAyat >= 1 && numAyat <= 5) numPage = 404;
                else if (numAyat >= 6 && numAyat <= 15) numPage = 405;
                else if (numAyat >= 16 && numAyat <= 24) numPage = 406;
                else if (numAyat >= 25 && numAyat <= 32) numPage = 407;
                else if (numAyat >= 33 && numAyat <= 41) numPage = 408;
                else if (numAyat >= 42 && numAyat <= 50) numPage = 409;
                else if (numAyat >= 51 && numAyat <= 60) numPage = 410;
                break;

            case 31:
                if (numAyat >= 1 && numAyat <= 11) numPage = 411;
                else if (numAyat >= 12 && numAyat <= 19) numPage = 412;
                else if (numAyat >= 20 && numAyat <= 28) numPage = 413;
                else if (numAyat >= 29 && numAyat <= 34) numPage = 414;
                break;

            case 32:
                if (numAyat >= 1 && numAyat <= 11) numPage = 415;
                else if (numAyat >= 12 && numAyat <= 20) numPage = 416;
                else if (numAyat >= 21 && numAyat <= 30) numPage = 417;
                break;

            case 33:
                if (numAyat >= 1 && numAyat <= 6) numPage = 418;
                else if (numAyat >= 7 && numAyat <= 15) numPage = 419;
                else if (numAyat >= 16 && numAyat <= 22) numPage = 420;
                else if (numAyat >= 23 && numAyat <= 30) numPage = 421;
                else if (numAyat >= 31 && numAyat <= 35) numPage = 422;
                else if (numAyat >= 36 && numAyat <= 43) numPage = 423;
                else if (numAyat >= 44 && numAyat <= 50) numPage = 424;
                else if (numAyat >= 51 && numAyat <= 54) numPage = 425;
                else if (numAyat >= 55 && numAyat <= 62) numPage = 426;
                else if (numAyat >= 63 && numAyat <= 73) numPage = 427;
                break;

            case 34:
                if (numAyat >= 1 && numAyat <= 7) numPage = 428;
                else if (numAyat >= 8 && numAyat <= 14) numPage = 429;
                else if (numAyat >= 15 && numAyat <= 22) numPage = 430;
                else if (numAyat >= 23 && numAyat <= 31) numPage = 431;
                else if (numAyat >= 32 && numAyat <= 39) numPage = 432;
                else if (numAyat >= 40 && numAyat <= 48) numPage = 433;
                else if (numAyat >= 49 && numAyat <= 54) numPage = 434;
                break;

            case 35:
                if (numAyat >= 1 && numAyat <= 3) numPage = 434;
                else if (numAyat >= 4 && numAyat <= 11) numPage = 435;
                else if (numAyat >= 12 && numAyat <= 18) numPage = 436;
                else if (numAyat >= 19 && numAyat <= 30) numPage = 437;
                else if (numAyat >= 31 && numAyat <= 38) numPage = 438;
                else if (numAyat == 39) numPage = 439;
                break;

            case 36:
                if (numAyat >= 1 && numAyat <= 12) numPage = 440;
                else if (numAyat >= 13 && numAyat <= 27) numPage = 441;
                else if (numAyat >= 28 && numAyat <= 40) numPage = 442;
                else if (numAyat >= 41 && numAyat <= 54) numPage = 443;
                else if (numAyat >= 55 && numAyat <= 70) numPage = 444;
                else if (numAyat >= 71 && numAyat <= 83) numPage = 445;
                break;

            case 37:
                if (numAyat >= 1 && numAyat <= 24) numPage = 446;
                else if (numAyat >= 25 && numAyat <= 51) numPage = 447;
                else if (numAyat >= 52 && numAyat <= 76) numPage = 448;
                else if (numAyat >= 77 && numAyat <= 102) numPage = 449;
                else if (numAyat >= 103 && numAyat <= 126) numPage = 450;
                else if (numAyat >= 127 && numAyat <= 153) numPage = 451;
                else if (numAyat >= 154 && numAyat <= 182) numPage = 452;
                break;

            case 38:
                if (numAyat >= 1 && numAyat <= 16) numPage = 453;
                else if (numAyat >= 17 && numAyat <= 26) numPage = 454;
                else if (numAyat >= 27 && numAyat <= 42) numPage = 455;
                else if (numAyat >= 43 && numAyat <= 61) numPage = 456;
                else if (numAyat >= 62 && numAyat <= 83) numPage = 457;
                else if (numAyat >= 84 && numAyat <= 88) numPage = 458;
                break;

            case 39:
                if (numAyat >= 1 && numAyat <= 5) numPage = 458;
                else if (numAyat >= 6 && numAyat <= 10) numPage = 459;
                else if (numAyat >= 11 && numAyat <= 21) numPage = 460;
                else if (numAyat >= 22 && numAyat <= 31) numPage = 461;
                else if (numAyat >= 32 && numAyat <= 40) numPage = 462;
                else if (numAyat >= 41 && numAyat <= 47) numPage = 463;
                else if (numAyat >= 48 && numAyat <= 56) numPage = 464;
                else if (numAyat >= 57 && numAyat <= 67) numPage = 465;
                else if (numAyat == 75) numPage = 467;  // учтем "аят 75"
                break;

            case 40:
                if (numAyat >= 1 && numAyat <= 7) numPage = 467;
                else if (numAyat >= 8 && numAyat <= 16) numPage = 468;
                else if (numAyat >= 17 && numAyat <= 25) numPage = 469;
                else if (numAyat >= 26 && numAyat <= 33) numPage = 470;
                else if (numAyat >= 34 && numAyat <= 40) numPage = 471;
                else if (numAyat >= 41 && numAyat <= 49) numPage = 472;
                else if (numAyat >= 50 && numAyat <= 58) numPage = 473;
                else if (numAyat >= 59 && numAyat <= 66) numPage = 474;
                else if (numAyat >= 67 && numAyat <= 77) numPage = 475;
                else if (numAyat >= 78 && numAyat <= 85) numPage = 476;
                break;

            case 41:
                if (numAyat >= 1 && numAyat <= 11) numPage = 477;
                else if (numAyat >= 12 && numAyat <= 20) numPage = 478;
                else if (numAyat >= 21 && numAyat <= 29) numPage = 479;
                else if (numAyat >= 30 && numAyat <= 38) numPage = 480;
                else if (numAyat >= 39 && numAyat <= 46) numPage = 481;
                else if (numAyat >= 47 && numAyat <= 54) numPage = 482;
                break;

            case 42:
                if (numAyat >= 1 && numAyat <= 10) numPage = 483;
                else if (numAyat >= 11 && numAyat <= 15) numPage = 484;
                else if (numAyat >= 16 && numAyat <= 22) numPage = 485;
                else if (numAyat >= 23 && numAyat <= 31) numPage = 486;
                else if (numAyat >= 32 && numAyat <= 44) numPage = 487;
                else if (numAyat >= 45 && numAyat <= 51) numPage = 488;
                else if (numAyat >= 52 && numAyat <= 53) numPage = 489;
                break;

            case 43:
                if (numAyat >= 1 && numAyat <= 10) numPage = 489;
                else if (numAyat >= 11 && numAyat <= 22) numPage = 490;
                else if (numAyat >= 23 && numAyat <= 33) numPage = 491;
                else if (numAyat >= 34 && numAyat <= 47) numPage = 492;
                else if (numAyat >= 48 && numAyat <= 60) numPage = 493;
                else if (numAyat >= 61 && numAyat <= 73) numPage = 494;
                else if (numAyat >= 74 && numAyat <= 89) numPage = 495;
                break;

            case 44:
                if (numAyat >= 1 && numAyat <= 18) numPage = 496;
                else if (numAyat >= 19 && numAyat <= 39) numPage = 497;
                else if (numAyat >= 40 && numAyat <= 59) numPage = 498;
                break;

            case 45:
                if (numAyat >= 1 && numAyat <= 13) numPage = 499;
                else if (numAyat >= 14 && numAyat <= 22) numPage = 500;
                else if (numAyat >= 23 && numAyat <= 32) numPage = 501;
                else if (numAyat >= 33 && numAyat <= 37) numPage = 502;
                break;

            case 46:
                if (numAyat >= 1 && numAyat <= 5) numPage = 502;
                else if (numAyat >= 6 && numAyat <= 14) numPage = 503;
                else if (numAyat >= 15 && numAyat <= 20) numPage = 504;
                else if (numAyat >= 21 && numAyat <= 28) numPage = 505;
                else if (numAyat >= 29 && numAyat <= 35) numPage = 506;
                break;

            case 47:
                if (numAyat >= 1 && numAyat <= 11) numPage = 507;
                else if (numAyat >= 12 && numAyat <= 19) numPage = 508;
                else if (numAyat >= 20 && numAyat <= 29) numPage = 509;
                else if (numAyat >= 30 && numAyat <= 38) numPage = 510;
                break;

            case 48:
                if (numAyat >= 1 && numAyat <= 9) numPage = 511;
                else if (numAyat >= 10 && numAyat <= 15) numPage = 512;
                else if (numAyat >= 16 && numAyat <= 23) numPage = 513;
                else if (numAyat >= 24 && numAyat <= 28) numPage = 514;
                else if (numAyat == 29) numPage = 515;  // учтем "аят 29"
                break;

            case 49:
                if (numAyat >= 1 && numAyat <= 4) numPage = 515;
                else if (numAyat >= 5 && numAyat <= 11) numPage = 516;
                else if (numAyat >= 12 && numAyat <= 18) numPage = 517;
                break;

            case 50:
                if (numAyat >= 1 && numAyat <= 15) numPage = 518;
                else if (numAyat >= 16 && numAyat <= 35) numPage = 519;
                else if (numAyat >= 36 && numAyat <= 45) numPage = 520;
                break;

            case 51:
                if (numAyat >= 1 && numAyat <= 6) numPage = 520;
                else if (numAyat >= 7 && numAyat <= 30) numPage = 521;
                else if (numAyat >= 31 && numAyat <= 51) numPage = 522;
                else if (numAyat >= 52 && numAyat <= 60) numPage = 523;
                break;

            case 52:
                if (numAyat >= 1 && numAyat <= 14) numPage = 523;
                else if (numAyat >= 15 && numAyat <= 31) numPage = 524;
                else if (numAyat >= 32 && numAyat <= 49) numPage = 525;
                break;

            case 53:
                if (numAyat >= 1 && numAyat <= 26) numPage = 526;
                else if (numAyat >= 27 && numAyat <= 44) numPage = 527;
                else if (numAyat >= 45 && numAyat <= 62) numPage = 528;
                break;

            case 54:
                if (numAyat >= 1 && numAyat <= 6) numPage = 528;
                else if (numAyat >= 7 && numAyat <= 27) numPage = 529;
                else if (numAyat >= 28 && numAyat <= 49) numPage = 530;
                else if (numAyat >= 50 && numAyat <= 55) numPage = 531;
                break;

            case 55:
                if (numAyat >= 1 && numAyat <= 16) numPage = 531;
                else if (numAyat >= 17 && numAyat <= 40) numPage = 532;
                else if (numAyat >= 41 && numAyat <= 67) numPage = 533;
                else if (numAyat >= 68 && numAyat <= 78) numPage = 534;
                break;

            case 56:
                if (numAyat >= 1 && numAyat <= 16) numPage = 534;
                else if (numAyat >= 17 && numAyat <= 50) numPage = 535;
                else if (numAyat >= 51 && numAyat <= 76) numPage = 536;
                else if (numAyat >= 77 && numAyat <= 96) numPage = 537;
                break;

            case 57:
                if (numAyat >= 1 && numAyat <= 3) numPage = 537;
                else if (numAyat >= 4 && numAyat <= 11) numPage = 538;
                else if (numAyat >= 12 && numAyat <= 18) numPage = 539;
                else if (numAyat >= 19 && numAyat <= 24) numPage = 540;
                else if (numAyat >= 25 && numAyat <= 29) numPage = 541;
                break;

            case 58:
                if (numAyat >= 1 && numAyat <= 6) numPage = 542;
                else if (numAyat >= 7 && numAyat <= 11) numPage = 543;
                else if (numAyat >= 12 && numAyat <= 21) numPage = 544;
                else if (numAyat == 22) numPage = 545;  // учтем "аят 22"
                break;

            case 59:
                if (numAyat >= 1 && numAyat <= 3) numPage = 545;
                else if (numAyat >= 4 && numAyat <= 9) numPage = 546;
                else if (numAyat >= 10 && numAyat <= 16) numPage = 547;
                else if (numAyat >= 17 && numAyat <= 24) numPage = 548;
                break;

            case 60:
                if (numAyat >= 1 && numAyat <= 5) numPage = 549;
                else if (numAyat >= 6 && numAyat <= 11) numPage = 550;
                else if (numAyat == 12 || numAyat == 13) numPage = 551;  // учтем "аяты 12-13"
                break;

            case 61:
                if (numAyat >= 1 && numAyat <= 5) numPage = 551;
                else if (numAyat >= 6 && numAyat <= 14) numPage = 552;
                break;

            case 62:
                if (numAyat >= 1 && numAyat <= 8) numPage = 553;
                else if (numAyat >= 9 && numAyat <= 11) numPage = 554;
                break;

            case 63:
                if (numAyat >= 1 && numAyat <= 4) numPage = 554;
                else if (numAyat >= 5 && numAyat <= 11) numPage = 555;
                break;

            case 64:
                if (numAyat >= 1 && numAyat <= 9) numPage = 556;
                else if (numAyat >= 10 && numAyat <= 18) numPage = 557;
                break;

            case 65:
                if (numAyat >= 1 && numAyat <= 5) numPage = 558;
                else if (numAyat >= 6 && numAyat <= 12) numPage = 559;
                break;

            case 66:
                if (numAyat >= 1 && numAyat <= 7) numPage = 560;
                else if (numAyat >= 8 && numAyat <= 12) numPage = 561;
                break;

            case 67:
                if (numAyat >= 1 && numAyat <= 12) numPage = 562;
                else if (numAyat >= 13 && numAyat <= 26) numPage = 563;
                else if (numAyat >= 27 && numAyat <= 30) numPage = 564;
                break;

            case 68:
                if (numAyat >= 1 && numAyat <= 15) numPage = 564;
                else if (numAyat >= 16 && numAyat <= 42) numPage = 565;
                else if (numAyat >= 43 && numAyat <= 52) numPage = 566;
                break;

            case 69:
                if (numAyat >= 1 && numAyat <= 8) numPage = 566;
                else if (numAyat >= 9 && numAyat <= 34) numPage = 567;
                else if (numAyat >= 35 && numAyat <= 52) numPage = 568;
                break;

            case 70:
                if (numAyat >= 1 && numAyat <= 10) numPage = 568;
                else if (numAyat >= 11 && numAyat <= 39) numPage = 569;
                else if (numAyat >= 40 && numAyat <= 44) numPage = 570;
                break;

            case 71:
                if (numAyat >= 1 && numAyat <= 10) numPage = 570;
                else if (numAyat >= 11 && numAyat <= 28) numPage = 571;
                break;

            case 72:
                if (numAyat >= 1 && numAyat <= 13) numPage = 572;
                else if (numAyat >= 14 && numAyat <= 28) numPage = 573;
                break;

            case 73:
                if (numAyat >= 1 && numAyat <= 19) numPage = 574;
                else if (numAyat == 20) numPage = 575;  // учтем "аят 20"
                break;

            case 74:
                if (numAyat >= 1 && numAyat <= 17) numPage = 575;
                else if (numAyat >= 18 && numAyat <= 47) numPage = 576;
                else if (numAyat >= 48 && numAyat <= 56) numPage = 577;
                break;

            case 75:
                if (numAyat >= 1 && numAyat <= 19) numPage = 577;
                else if (numAyat >= 20 && numAyat <= 30) numPage = 578;
                break;

            case 76:
                if (numAyat >= 1 && numAyat <= 5) numPage = 578;
                else if (numAyat >= 6 && numAyat <= 25) numPage = 579;
                else if (numAyat >= 26 && numAyat <= 31) numPage = 580;
                break;

            case 77:
                if (numAyat >= 1 && numAyat <= 19) numPage = 580;
                else if (numAyat >= 20 && numAyat <= 40) numPage = 581;
                break;

            case 78:
                if (numAyat >= 1 && numAyat <= 30) numPage = 582;
                else if (numAyat >= 31 && numAyat <= 40) numPage = 583;
                break;

            case 79:
                if (numAyat >= 1 && numAyat <= 15) numPage = 583;
                else if (numAyat >= 16 && numAyat <= 46) numPage = 584;
                break;

            case 80:
                if (numAyat >= 1 && numAyat <= 42) numPage = 585;
                break;

            case 81:
                if (numAyat >= 1 && numAyat <= 29) numPage = 586;
                break;

            case 82:
                if (numAyat >= 1 && numAyat <= 19) numPage = 587;
                break;

            case 83:
                if (numAyat >= 1 && numAyat <= 6) numPage = 587;
                else if (numAyat >= 7 && numAyat <= 34) numPage = 588;
                if (numAyat == 35) numPage = 589;
                break;

            case 84:
                if (numAyat >= 1 && numAyat <= 25) numPage = 589;
                break;

            case 85:
                if (numAyat >= 1 && numAyat <= 22) numPage = 590;
                break;

            case 86:
                if (numAyat >= 1 && numAyat <= 17) numPage = 591;
                break;

            case 87:
                if (numAyat >= 1 && numAyat <= 15) numPage = 591;
                else if (numAyat >= 16 && numAyat <= 19) numPage = 592;
                break;

            case 88:
                if (numAyat >= 1 && numAyat <= 26) numPage = 592;
                break;

            case 89:
                if (numAyat >= 1 && numAyat <= 23) numPage = 593;
                else if (numAyat >= 24 && numAyat <= 30) numPage = 594;
                break;

            case 90:
                if (numAyat >= 1 && numAyat <= 20) numPage = 594;
                break;

            case 91:
                if (numAyat >= 1 && numAyat <= 15) numPage = 595;
                break;

            case 92:
                if (numAyat >= 1 && numAyat <= 14) numPage = 595;
                else if (numAyat >= 15 && numAyat <= 21) numPage = 596;
                break;

            case 93:
                if (numAyat >= 1 && numAyat <= 11) numPage = 596;
                break;

            case 94:
                if (numAyat >= 1 && numAyat <= 8) numPage = 596;
                break;

            case 95:
                if (numAyat >= 1 && numAyat <= 8) numPage = 597;
                break;

            case 96:
                if (numAyat >= 1 && numAyat <= 19) numPage = 597;
                break;

            case 97:
                if (numAyat >= 1 && numAyat <= 5) numPage = 598;
                break;

            case 98:
                if (numAyat >= 1 && numAyat <= 7) numPage = 598;
                else if (numAyat == 8) numPage = 599;
                break;

            case 99:
                if (numAyat >= 1 && numAyat <= 8) numPage = 599;
                break;

            case 100:
                if (numAyat >= 1 && numAyat <= 9) numPage = 599;
                else if (numAyat >= 10 && numAyat <= 11) numPage = 600;
                break;

            case 101:
                if (numAyat >= 1 && numAyat <= 11) numPage = 600;
                break;

            case 102:
                if (numAyat >= 1 && numAyat <= 8) numPage = 600;
                break;

            case 103:
                if (numAyat >= 1 && numAyat <= 3) numPage = 601;
                break;

            case 104:
                if (numAyat >= 1 && numAyat <= 9) numPage = 601;
                break;

            case 105:
                if (numAyat >= 1 && numAyat <= 5) numPage = 601;
                break;

            case 106:
                if (numAyat >= 1 && numAyat <= 4) numPage = 602;
                break;

            case 107:
                if (numAyat >= 1 && numAyat <= 7) numPage = 602;
                break;

            case 108:
                if (numAyat >= 1 && numAyat <= 3) numPage = 602;
                break;

            case 109:
                if (numAyat >= 1 && numAyat <= 6) numPage = 603;
                break;

            case 110:
                if (numAyat >= 1 && numAyat <= 3) numPage = 603;
                break;

            case 111:
                if (numAyat >= 1 && numAyat <= 5) numPage = 603;
                break;

            case 112:
                if (numAyat >= 1 && numAyat <= 4) numPage = 604;
                break;

            case 113:
                if (numAyat >= 1 && numAyat <= 5) numPage = 604;
                break;

            case 114:
                if (numAyat >= 1 && numAyat <= 6) numPage = 604;
                break;


        }
        return numPage;
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

    public void updateBookmarkIcon(int page, MenuItem menuItem) {
        try {
            // Проверка наличия закладки на текущей странице
            BookmarkAdapter bookmarkAdapter = BookmarkAdapter.this;
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

    public interface BookmarkCallback {
        void onBookmarkRemoved(int page);
    }

}