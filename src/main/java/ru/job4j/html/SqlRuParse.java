package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.Parse;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".postslisttopic");
            for (Element el : row) {
                if (el.text().contains("Важно")) {
                    continue;
                }
                rsl.add(detail(el.children().attr("href")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override

    public Post detail(String link) {
        Post post = new Post();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".messageHeader");
            Element name = row.first();

            post.setTitle(name.text());

            post.setLink(link);

            post.setDescription(loadData(link));

            String[] date1 = name.parent().parent().children().get(2).text().split(", ");
            String[] date2 = date1[1].split(" ");
            String rsl = date1[0] + ", " + date2[0];

            post.setCreated(dateTimeParser.parse(rsl));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    public static String loadData(String url) throws Exception {
        Document doc = Jsoup.connect(url).get();
        Elements row = doc.select(".msgBody");
        Element message = row.get(1);

        Elements row1 = doc.select(".msgFooter");
        Element date = row1.get(0);

        String[] dateArray = date.text().split(", ");
        String[] dateArray2 = dateArray[1].split(" ");
        String day = dateArray[0] + ", " + dateArray2[0];

        return message.text() + " ДАТА СООБЩЕНИЯ: " + day;
    }

    public static void main(String[] args) throws Exception {
        DateTimeParser date = new SqlRuDateTimeParser();
        SqlRuParse sql = new SqlRuParse(date);
        List<Post> rsl = sql.list("https://www.sql.ru/forum/job-offers/1");
        System.out.println(rsl.get(6).getCreated());
    }
}


