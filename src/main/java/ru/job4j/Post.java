package ru.job4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {

    private int id = Statement.RETURN_GENERATED_KEYS;

    private String title;

    private String link;

    private String description;

    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(title, post.title) && Objects.equals(link, post.link)
                && Objects.equals(created, post.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link, created);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", created=" + created
                + '}';
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
       String exp = loadData("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");

            System.out.println(exp);

    }
}
