package com.example.schedule.ui;

import static com.example.schedule.ui.until.FormatString.formating;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schedule.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private boolean offline = false; // Работа в онлайн режиме
    public String request;
    private String WeekNumber;
    private int count = 0;
    private TextView weeknumber;
    private EditText timetable;
    private Button next;
    private Button down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        nextBtnInit();
        downBtnInit();
        getting getting = new getting();
        getting.execute();
    }

    private void initView() {
        weeknumber = findViewById(R.id.WeekNumber);
        timetable = findViewById(R.id.timetable);
        next = findViewById(R.id.next);
        down = findViewById(R.id.down);
    }

    //События для кнопок назад
    private void downBtnInit() {
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, count);
                Date dayformat = calendar.getTime();
                SimpleDateFormat format = new SimpleDateFormat("dd MMMM");
                formating(
                        format.format(dayformat),
                        request,
                        WeekNumber,
                        timetable,
                        weeknumber
                );
            }
        });
    }

    //События для кнопок вперёд
    private void nextBtnInit() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, count);
                Date dayformat = calendar.getTime();
                SimpleDateFormat format = new SimpleDateFormat("dd MMMM");
                formating(
                        format.format(dayformat),
                        request,
                        WeekNumber,
                        timetable,
                        weeknumber
                );
            }
        });
    }

    private class getting extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSupportActionBar().setTitle("Загрузка...");
        }

        @Override
        protected String doInBackground(String... params) {
            //Обращение к сайту с расписанием
            String answer = "";
            String url = "https://web.archive.org/web/20200811123003/https://ictis.sfedu.ru/rasp/HTML/82.htm";// Адрес сайта с расписанием
            Document document = null;
            try {
                // Получение данных
                document = Jsoup.connect(url).get();
                answer = document.body().html();
            } catch (IOException e) {
                // Если произошла ошибка, значит вероятнее всего, отсутствует соединение с интернетом
                // Загружаем в переменную answer офлайн версию из txt файла
                try {
                    BufferedReader read = new BufferedReader(new InputStreamReader(openFileInput("timetable.txt")));
                    String str = " ";
                    while ((str = read.readLine()) != null) {
                        answer += str;
                    }
                    read.close();
                    offline = true;//работаем в оффлайн режиме
                } catch (FileNotFoundException ex) {
                    //Если файла с сохранённым расписанием нет, то записываем в answer пустоту
                    answer = " ";
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            //Очистка лишнего текста из html
            answer = answer.replace("Пары", "")
                    .replace("Время", "")
                    .replace("<br>", "br")
                    .replace("<font face=\"Arial\" size=\"1\"></font><p align=\"CENTER\"><font face=\"Arial\" size=\"1\"></font>", "nolessone")
                    .replace("<!-- <TD WIDTH=\"11%\" VALIGN=\"TOP\" HEIGHT=28> _ --!>", "")
                    .replace("  ", "");
            return Jsoup.parse(answer).text();//Вытаскиваем текст из кода в переменной answer и передаём в UI-поток
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /*Возвращение данных из потока*/
            request = "";
            String temp = result.toString();
            // Запись содержимого в файл timetable.txt, в котором хранится оффлайн версия расписания.
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(openFileOutput("timetable.txt", MODE_PRIVATE)));
                writer.write(temp);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean start = false;
            for (String str : temp.split("Неделя: ")) {
                if (start) {
                    //В начало каждой недели добавляем слово newweek и добавляем в request
                    request += "newweek" + str.split("Расписание")[0] + "\n";
                }
                start = true;
            }
            // Добавляем к дням недели приставку newday, для дальнейшей разбивки строки
            request = request.replace("Пнд", "newdayПнд").replace("Втр", "newdayВтр")
                    .replace("Срд", "newdayСрд").replace("Чтв", "newdayЧтв")
                    .replace("Птн", "newdayПтн").replace("Сбт", "newdayСбт")
                    .replace("Вск", "newdayВск");
            /*Получаем дату дня
            Если count = 0, то вернётся дата сегодняшнего дня
            Если count = -1, то вчерашнего
            Если count = 1, то завтрашнего и т.д
             */
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, count);
            Date dayformat = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM");
            //Вызываем функцию, которая будет заниматься представлением данных
            formating(
                    format.format(dayformat),
                    request,
                    WeekNumber,
                    timetable,
                    weeknumber
            );
            if (offline && !temp.equals("")) {
                //Уведомляем пользователя, что загружена оффлайн версия расписания
                Toast.makeText(getApplicationContext(), "Загружена оффлайн версия расписания!", Toast.LENGTH_LONG).show();
            }
            //Если наш ответ равен пустоте, значит произошла ошибка
            if (temp.equals("")) {
                Toast.makeText(getApplicationContext(), "Произошла ошибка!", Toast.LENGTH_LONG).show();
            }
            getSupportActionBar().setTitle("Готово");
        }
    }
}