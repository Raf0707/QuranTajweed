package raf.tabiin.qurantajweed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import raf.tabiin.qurantajweed.adapters.BookmarkAdapter;
import raf.tabiin.qurantajweed.adapters.ImageAdapter;
import raf.tabiin.qurantajweed.details.BookmarkActivity;
import raf.tabiin.qurantajweed.model.Bookmark;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BookmarkAdapter bookmarkAdapter;

    private Integer[] numPageSures = new Integer[]{4, 5, 53, 80, 109, 131, 154, 180, 190, 211, 224, 328, 252, 258, 265, 270, 285, 296, 308, 315, 325, 335,
            345, 353, 362, 370, 380, 388, 399, 407, 414, 418, 421, 431, 437, 443, 449, 456, 461, 470, 480, 486, 492, 499, 502, 505, 510, 514, 518, 521, 523, 526,
            529, 531, 534, 537, 540, 545, 548, 552, 554, 556, 557, 559, 661, 563, 565, 567, 569, 571, 573, 575, 577, 578, 580, 581, 583, 585, 586, 588, 589, 590,
            590, 592, 593, 594, 594, 595, 596, 597, 598, 598, 599, 599, 600, 600, 601, 601, 602, 602, 603, 603, 604, 604, 604, 605, 605, 605, 606, 606, 606, 607,
            607, 607, 608};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bookmarkAdapter = new BookmarkAdapter(this);
        viewPager.setLayoutDirection(ViewPager2.LAYOUT_DIRECTION_RTL);
        viewPager.setAdapter(new ImageAdapter(this, 605));

        bookmarkAdapter.loadBookmarks();

        // Установка текущей позиции из закладок, если они есть
        List<Bookmark> bookmarks = bookmarkAdapter.getBookmarks();
        if (bookmarks != null && !bookmarks.isEmpty()) {
            viewPager.post(() -> viewPager.setCurrentItem(bookmarks.get(0).getPosition(), false));
        }
    }

    /*private void initContent() {
        // Инициализация массива
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
        sures[11] = "12. «Йусуф» \n («Иосиф»)";
        sures[12] = "13. «Ар-Раад» \n («Гром»)";
        sures[13] = "14. «Ибрахим» \n («Авраам»)";
        sures[14] = "15. «Аль-Хиджр» \n («Хиджр»)";
        sures[15] = "16. «Ан-Нахль» \n («Пчёлы»)";
        sures[16] = "17. «Аль-Исра» \n («Ночной перенос»)";
        sures[17] = "18. «Аль-Кахф» \n («Пещера»)";
        sures[18] = "19. «Марьям» \n («Мария»)";
        sures[19] = "20. «Та Ха» \n («Та Ха»)";
        sures[20] = "21. «Аль-Анбийа» \n («Пророки»)";
        sures[21] = "22. «Аль-Хаджж» \n («Паломничество»)";
        sures[22] = "23. «Аль-Муминун» \n («Верующие»)";
        sures[23] = "24. «Ан-Нур» \n («Свет»)";
        sures[24] = "25. «Аль-Фуркан» \n («Различение»)";
        sures[25] = "26. «Аш-Шуара» \n («Поэты»)";
        sures[26] = "27. «Ан-Намль» \n («Муравьи»)";
        sures[27] = "28. «Аль-Касас» \n («Рассказ»)";
        sures[28] = "29. «Аль-Анкабут» \n («Паук»)";
        sures[29] = "30. «Ар-Рум» \n («Римляне»)";
        sures[30] = "31. «Лукман» \n («Лукман»)";
        sures[31] = "32. «Ас-Саджда» \n («Поклон»)";
        sures[32] = "33. «Аль-Ахзаб» \n («Сонмы»)";
        sures[33] = "34. «Саба» \n («Саба»)";
        sures[34] = "35. «Фатыр» \n («Ангелы»)";
        sures[35] = "36. «Ясин» \n («Йа Син»)";
        sures[36] = "37. «Ас-Саффат» \n («Стоящие в ряд»)";
        sures[37] = "38. «Сад» \n («Сад»)";
        sures[38] = "39. «Аз-Зумар» \n («Толпы»)";
        sures[39] = "40. «Гафир» \n («Прощающий»)";
        sures[40] = "41. «Фуссилат» \n («Разъяснены»)";
        sures[41] = "42. «Аш-Шура» \n («Совет»)";
        sures[42] = "43. «Аз-Зухруф» \n («Украшения»)";
        sures[43] = "44. «Ад-Духан» \n («Дым»)";
        sures[44] = "45. «Аль-Джасия» \n («Коленопреклонённые»)";
        sures[45] = "46. «Аль-Ахкаф» \n («Пески»)";
        sures[46] = "47. «Мухаммад» \n («Мухаммад»)";
        sures[47] = "48. «Аль-Фатх» \n («Победа»)";
        sures[48] = "49. «Аль-Худжурат» \n («Комнаты»)";
        sures[49] = "50. «Каф» \n («Каф»)";
        sures[50] = "51. «Аз-Зарият» \n («Рассеивающие»)";
        sures[51] = "52. «Ат-Тур» \n («Гора»)";
        sures[52] = "53. «Ан-Наджм» \n («Звезда»)";
        sures[53] = "54. «Аль-Камар» \n («Месяц»)";
        sures[54] = "55. «Ар-Рахман» \n («Милосердный»)";
        sures[55] = "56. «Аль-Вакиа» \n («Падающее»)";
        sures[56] = "57. «Аль-Хадид» \n («Железо»)";
        sures[57] = "58. «Аль-Муджадила» \n («Препирательство»)";
        sures[58] = "59. «Аль-Хашр» \n («Собрание»)";
        sures[59] = "60. «Аль-Мумтахана» \n («Испытуемая»)";
        sures[60] = "61. «Ас-Сафф» \n («Ряды»)";
        sures[61] = "62. «Аль-Джума» \n («Пятница»)";
        sures[62] = "63. «Аль-Мунафикун» \n («Лицемеры»)";
        sures[63] = "64. «Ат-Тагабун» \n («Взаимное обманывание»)";
        sures[64] = "65. «Ат-Талак» \n («Развод»)";
        sures[65] = "66. «Ат-Тахрим» \n («Запрещение»)";
        sures[66] = "67. «Аль-Мульк» \n («Власть»)";
        sures[67] = "68. «Аль-Калям» \n («Письменная трость»)";
        sures[68] = "69. «Аль-Хакка» \n («Неизбежное»)";
        sures[69] = "70. «Аль-Мааридж» \n («Ступени»)";
        sures[70] = "71. «Нух» \n («Нух»)";
        sures[71] = "72. «Аль-Джинн» \n («Джинны»)";
        sures[72] = "73. «Аль-Муззаммиль» \n («Закутавшийся»)";
        sures[73] = "74. «Аль-Муддассир» \n («Завернувшийся»)";
        sures[74] = "75. «Аль-Кийама» \n («Воскресение»)";
        sures[75] = "76. «Аль-Инсан» \n («Человек»)";
        sures[76] = "77. «Аль-Мурсалят» \n («Посылаемые»)";
        sures[77] = "78. «Ан-Наба» \n («Весть»)";
        sures[78] = "79. «Ан-Назиат» \n («Вырывающие»)";
        sures[79] = "80. «Абаса» \n («Нахмурился»)";
        sures[80] = "81. «Ат-Таквир» \n («Скручивание»)";
        sures[81] = "82. «Аль-Инфитар» \n («Раскалывание»)";
        sures[82] = "83. «Аль-Мутаффифин» \n («Обвешивающие»)";
        sures[83] = "84. «Аль-Иншикак» \n («Разверзнется»)";
        sures[84] = "85. «Аль-Бурудж» \n («Башни»)";
        sures[85] = "86. «Ат-Тарик» \n («Ночной путник»)";
        sures[86] = "87. «Аль-Аля» \n («Высочайший»)";
        sures[87] = "88. «Аль-Гашия» \n («Покрывающее»)";
        sures[88] = "89. «Аль-Фаджр» \n («Заря»)";
        sures[89] = "90. «Аль-Балад» \n («Город»)";
        sures[90] = "91. «Аш-Шамс» \n («Солнце»)";
        sures[91] = "92. «Аль-Лайл» \n («Ночь»)";
        sures[92] = "93. «Ад-Духа» \n («Утро»)";
        sures[93] = "94. «Аш-Шарх» \n («Раскрытие»)";
        sures[94] = "95 «Ат-Тин» \n («Смоковница»)";
        sures[95] = "96. «Аль-Алак» \n («Сгусток»)";
        sures[96] = "97. «Аль-Кадр» \n («Могущество»)";
        sures[97] = "98. «Аль-Баййина» \n («Ясное знамение»)";
        sures[98] = "99. «Аз-Залзала» \n («Землетрясение»)";
        sures[99] = "100. «Аль-Адият» \n («Скачущие»)";
        sures[100] = "101. «Аль-Кари’а» \n («Поражающее»)";
        sures[101] = "102. «Ат-Такасур» \n («Приумножение»)";
        sures[102] = "103. «Аль-Аср» \n («Предвечернее время»)";
        sures[103] = "104. «Аль-Хумаза» \n («Хулитель»)";
        sures[104] = "105. «Аль-Филь» \n («Слон»)";
        sures[105] = "106. «Курайш» \n («Курайшиты»)";
        sures[106] = "107. «Аль-Маун» \n («Милостыня»)";
        sures[107] = "108. «Аль-Каусар» \n («Изобилие»)";
        sures[108] = "109. «Аль-Кафирун» \n («Неверующие»)";
        sures[109] = "110. «Ан-Наср» \n («Помощь»)";
        sures[110] = "111. «Аль-Масад» \n («Пальмовые волокна»)";
        sures[111] = "112. «Аль-Ихлас» \n («Очищение веры»)";
        sures[112] = "113. «Аль-Фаляк» \n («Рассвет»)";
        sures[113] = "114. «Ан-Нас» \n («Люди»)";
        sures[114] = "Дуа после прочтения Корана";
    }

    private void initMap() {
        // Инициализация HashMap
        for(int n = 0; n < sures.length; ++n) {
            suresName.add(new KoranItemContent(sures[n], numPageSures[n] - 1));
        }


    }*/

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastPosition", viewPager.getCurrentItem());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastPosition", viewPager.getCurrentItem());
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int lastPosition = preferences.getInt("lastPosition", 0); // Значение по умолчанию - 0
        viewPager.setCurrentItem(lastPosition, false); // Установка сохраненной позиции
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_bookmarks) {
            Intent intent = new Intent(this, BookmarkActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}