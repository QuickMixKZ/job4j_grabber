package ru.job4j.grabber.interfaces;

import ru.job4j.grabber.models.Post;

import java.util.List;

public interface Store {
    void save(Post post);

    List<Post> getAll();

    Post findById(int id);
}