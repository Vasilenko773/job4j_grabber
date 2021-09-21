package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.Post;
import ru.job4j.grabber.utils.Parse;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".forumTable");

            for (Element el : row) {

              //  System.out.println(el.children().attr("href"));


                if (el.text().contains("Важно")) {
                    continue;
                }
                SqlRuParse sql = new SqlRuParse();
             ///  System.out.println(el.parent().attr("href"));
            //    rsl.add(sql.detail(el.children().attr("href")));

              /*  post.setTitle(ed.child(0).text());

                post.setLink(ed.child(0).attr("href"));

                SqlRuDateTimeParser sql = new SqlRuDateTimeParser();
                LocalDateTime date = sql.parse(ed.parent().children().get(5).text());
                post.setCreated(date);
                rsl.add(post);*/
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
             Elements row = doc.select(".postslisttopic");

            Element ed = row.first();

            post.setTitle(ed.child(0).text());

            post.setLink(ed.child(0).attr("href"));

            post.setDescription(loadData(post.getLink()));

            SqlRuDateTimeParser sql = new SqlRuDateTimeParser();
            LocalDateTime date = sql.parse(ed.parent().children().get(5).text());
            post.setCreated(date);

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

        SqlRuParse sql = new SqlRuParse();

       // Post exp = sql.detail("https://www.sql.ru/forum/job-offers/1");
           List<Post> rsl = sql.list("https://www.sql.ru/forum/job-offers/1");


      //  System.out.println(rsl.get(10).getTitle());
       // System.out.println(rsl.get(18).getTitle());
    }
    }


