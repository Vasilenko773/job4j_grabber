package ru.job4j.grabber;


import ru.job4j.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.SqlRuParse;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {

        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
            // Class.forName(cfg.getProperty(cfg.getProperty("hibernate.connection.driver_class")));
            cnn = DriverManager.getConnection(cfg.getProperty("hibernate.connection.url"),
                    cfg.getProperty("hibernate.connection.username"),
                    cfg.getProperty("hibernate.connection.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement("insert into posts(name, text, link, created) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();

            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    post.setId(resultSet.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement("select * from posts")) {
            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {
                    Post post = new Post();
                    post.setId(resultSet.getInt("id"));
                    post.setTitle(resultSet.getString("name"));
                    post.setDescription(resultSet.getString("text"));
                    post.setLink(resultSet.getString("link"));
                    post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                    rsl.add(post);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        Post post = new Post();
        try (PreparedStatement ps = cnn.prepareStatement("select * from posts where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    post.setId(resultSet.getInt(1));
                    post.setTitle(resultSet.getString("name"));
                    post.setDescription(resultSet.getString("text"));
                    post.setLink(resultSet.getString("link"));
                    post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        DateTimeParser date = new SqlRuDateTimeParser();
        SqlRuParse sql = new SqlRuParse(date);
        List<Post> posts = sql.list("https://www.sql.ru/forum/job-offers/1");
        Properties ps = new Properties();
        PsqlStore psql = new PsqlStore(ps);
        psql.save(posts.get(1));
          List<Post> sqlPost = psql.getAll();
        System.out.println(posts.get(1));
        System.out.println(sqlPost.get(0));
        System.out.println(psql.findById(1) == posts.get(1));
        System.out.println(posts.get(1).getCreated());
    }
}