package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.interfaces.Parse;
import ru.job4j.grabber.models.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements description = document.select(".job_show_description__vacancy_description").select(".style-ugc");
        return description.first().text();
    }

    @Override
    public List<Post> list(String link) {
        List<Post> result = new ArrayList<>();
        for (int page = 1; page <= 5; page++) {
            Connection connection = Jsoup.connect(String.format(link + "?page=%d", page));
            Document document = null;
            try {
                document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    result.add(parsePost(row));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    private Post parsePost(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        String date = element.select(".vacancy-card__date").first().child(0).attr("datetime");
        String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String description = "";
        try {
            description = retrieveDescription(vacancyLink);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalDateTime created = dateTimeParser.parse(date);
        return new Post(vacancyName, vacancyLink, description, created);
    }
}