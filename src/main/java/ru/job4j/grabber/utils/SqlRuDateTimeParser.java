package ru.job4j.grabber.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static Map<String, String> months = Map.ofEntries(
            Map.entry("янв", "1"),
            Map.entry("фев", "2"),
            Map.entry("мар", "3"),
            Map.entry("апр", "4"),
            Map.entry("май", "5"),
            Map.entry("июн", "6"),
            Map.entry("июл", "7"),
            Map.entry("авг", "8"),
            Map.entry("сен", "9"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String parse) {
        String[] wholeDate = parse.split(", ");
        String[] date = wholeDate[0].split(" ");
        LocalDate lD = null;

        if (date.length == 3) {
            lD = LocalDate.parse(String.format("%s %s %s", date[0], months.get(date[1]), date[2]), DateTimeFormatter.ofPattern("d M yy"));

        } else if (date[0].contains("сегодня")) {
            lD = LocalDate.now();

        } else {
            lD = LocalDate.now().minusDays(1);
        }
        LocalTime lT = LocalTime.parse(wholeDate[1], DateTimeFormatter.ofPattern("HH:mm"));

        return LocalDateTime.of(lD, lT);
    }

    public List<String> url() {
        ArrayList<String> rsl = new ArrayList<>();
        rsl.add("https://www.sql.ru/forum/job-offers");
        rsl.add("https://www.sql.ru/forum/job-offers/2");
        rsl.add("https://www.sql.ru/forum/job-offers/3");
        rsl.add("https://www.sql.ru/forum/job-offers/4");
        rsl.add("https://www.sql.ru/forum/job-offers/5");
        return rsl;
    }

    public static void main(String[] args) throws Exception {
        SqlRuDateTimeParser sqlRuDateTimeParser = new SqlRuDateTimeParser();
        List<String> pages = sqlRuDateTimeParser.url();
        for (String list : pages) {
            Document doc = Jsoup.connect(list).get();
            Elements row = doc.select("td[style].altCol");
            for (Element td : row) {

                LocalDateTime exp = sqlRuDateTimeParser.parse(td.text());
                System.out.println(exp);
            }
        }
    }
}
