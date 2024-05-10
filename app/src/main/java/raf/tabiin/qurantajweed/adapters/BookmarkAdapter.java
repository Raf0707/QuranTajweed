package raf.tabiin.qurantajweed.adapters;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
        holder.textView.setText("Страница " + page + "\n" + getSuraTitle(page));


        // Устанавливаем слушателя нажатий
        holder.itemView.setOnClickListener(view -> {
            // Вызывайте метод, чтобы переместить ViewPager2 на страницу, связанную с закладкой
            navigateToPage(bookmark.getPosition());
        });
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
            case 106: case 107: case 108:
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