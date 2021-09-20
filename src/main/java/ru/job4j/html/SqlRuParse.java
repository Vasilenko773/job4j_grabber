package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t").get();
        Elements row = doc.select(".msgTable");

        for (Element td : row) {
            Element message = td.children().get(0).children().get(1).children().get(1);
            Element date = td.children().get(0).children().get(2).children().get(0);
            String[] dateArray = date.text().split(", ");
            String[] dateArray2 = dateArray[1].split(" ");
            String day = dateArray[0] + ", " + dateArray2[0];
            System.out.println("СООБЩЕНИЕ: " + message.text() + "ДАТА: " + day);
        }
    }
}

