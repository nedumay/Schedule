package com.example.schedule.ui.until;

import android.widget.EditText;
import android.widget.TextView;

public class FormatString {

    public static void formating(
            String day,
            String request,
            String WeekNumber,
            EditText timetable,
            TextView weeknumber)
    {
        String DayTimetable = "";
        String[] weeks = request.split("newweek");
        String DayData = "";//Тут будет день недели и дата
        /*
        Переменные ниже будут содержать информацию о каждой паре
        Всего в день может быть семь пар
        массив less содержит названия каждого предмета
        массив tich содержит ФИО преподавателя каждого предмета
        массив aud содержит аудиторию, в которой будет проходить предмет (например Д-230)
         */
        String less[] = new String[365];
        String tich[] = new String[365];
        String aud[] = new String[365];
        for(String thisweek:weeks){//пробегаемся по неделям
            if(thisweek.indexOf(day) != -1) {//Если нужный нам день найден в этой неделе то...
                WeekNumber = thisweek.split(" ")[0];//Достаём номер недели
                for(String thisday:thisweek.split("newday")){//Теперь пробегаемся по дням этой недели
                    if(thisday.indexOf(day) != -1) {//Если данный день совпадает с нужным нам днём, то...
                        //Делаем так, тобы перед каждой парой была приставка newless
                        //пара всегда начинается с соответствующей приставки пр. лек. лаб. и пр.
                        thisday = thisday.replace("no","newless")
                                .replace("пр.","newlessпр.")
                                .replace("лек.","newlessлек.")
                                .replace("лаб.","newlessлаб.");
                        int i = 0;
                        for(String thislessone:thisday.split("newless")) {//Теперь пробегаемся по предметам данного дня
                            if(i != 0) {
                                String[] ScienceInformation = thislessone.replace("br ","").split("br");
                                String science = ScienceInformation[0];
                                science = science.replace("lessone","Окно");
                                String ticher = "";
                                if(ScienceInformation.length > 1)
                                    ticher = ScienceInformation[1];
                                DayTimetable += i + "-ая: Предмет - " + science+"\n"+ticher+"\n\n";
                                ticher = ticher.replace("А-","@А-").replace("Б-","@Б-")
                                        .replace("В-","@В-").replace("Г-","@Г-")
                                        .replace("Д-","@Д-").replace("Е-","@Е-")
                                        .replace("И-","@И-").replace("K-","@K-");
                                String Auditory;
                                if(ticher.split("@").length == 2){
                                    Auditory = "\nАудитория: "+ticher.split("@")[1];
                                }else
                                    Auditory = "\n";//На случай если пары нет
                                ticher = ticher.split("@")[0];
                                if(ticher.length() >0){
                                    ticher = "\nПреподаватель: " + ticher;
                                }else{
                                    ticher = "\nСамоподготовка ";
                                }
                                if(i==1){
                                    less[i-1] = "\n1-ая (8:00-9:30)\n"+science;
                                }
                                if(i==2){
                                    less[i-1] = "\n2-ая (9:40-11:10)\n"+science;
                                }
                                if(i==3){
                                    less[i-1] = "\n3-ая (11:20-12:50)\n"+science;
                                }
                                if(i==4){
                                    less[i-1] = "\n4-ая (13:20-14:50)\n"+science;
                                }
                                if(i==5){
                                    less[i-1] = "\n5-ая (15:00-16:30)\n"+science;
                                }
                                if(i==6){
                                    less[i-1] = "\n6-ая (16:50-18:20)\n"+science;
                                }
                                if(i==7){
                                    less[i-1] = "\n7-ая (18:30-20:00)\n"+science;
                                }
                                tich[i-1] = ticher;
                                aud[i-1] = Auditory;
                            }else
                                DayData = thislessone;//При i=0 в thislessone будет дата текущего дня
                            i++;
                        }
                    }
                }

            }
        }
        timetable.setText(DayData);//Выводим дату
        for(int i = 0; i <=6; i++){
            timetable.setText(timetable.getText()+"\n"+less[i]+tich[i]+aud[i]);//Вывод пары, препода и аудитории каждой пары (от нулевой до шестой)
        }
        weeknumber.setText(WeekNumber + " неделя ");//Выводим номер неддели
    }
}
