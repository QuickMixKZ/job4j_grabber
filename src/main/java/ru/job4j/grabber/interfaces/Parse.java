package ru.job4j.grabber.interfaces;

import ru.job4j.grabber.models.Post;

import java.io.IOException;
import java.util.List;

public interface Parse {
    List<Post> list(String link);
}