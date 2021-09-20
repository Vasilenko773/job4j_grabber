package ru.job4j;

import java.sql.Statement;
import java.time.LocalDateTime;
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
}
