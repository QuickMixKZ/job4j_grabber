package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.models.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=P", SOURCE_LINK);

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
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();
        for (int page = 1; page <= 20; page++) {
            Connection connection = Jsoup.connect(link.replace('P', (char) page));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String date = row.select(".vacancy-card__date").first().child(0).attr("datetime");
                String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = "";
                try {
                    description = retrieveDescription(vacancyLink);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LocalDateTime created = dateTimeParser.parse(date);
                result.add(new Post(0, vacancyName, vacancyLink, description, created));
            });
        }
        return result;
    }
}