package ru.job4j.grabber.utils;

import ru.job4j.Post;

import java.util.List;

public interface Parse {

    List<Post> list(String link);

    Post detail(String link);
}