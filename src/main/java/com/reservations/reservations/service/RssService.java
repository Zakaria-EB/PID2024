package com.reservations.reservations.service;


import com.reservations.reservations.model.RssItem;
import com.reservations.reservations.model.Show;
import com.reservations.reservations.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssService {

    @Autowired
    private ShowRepository showRepository;

    public List<RssItem> getUpcomingShows() {
        List<Show> shows = showRepository.findUpcomingShows();

        List<RssItem> rssItems = new ArrayList<>();

        for (Show show : shows) {
            String title = show.getTitle();
            String link = "http://localhost:8080/shows/" + show.getSlug();
            String description = show.getDescription()
                    + " — Lieu : " + show.getFullLocation()
                    + " — Prochaine date : " + show.getNextRepresentationDateFormatted();
            String author = show.getAuthorsAsString();
            String pubDate = show.getCreatedAt()
                    .atZone(ZoneId.systemDefault()) // ou use ZoneId.of("Europe/Paris") si tu veux forcer
                    .format(DateTimeFormatter.RFC_1123_DATE_TIME);
            String guid = String.valueOf(show.getId());

            rssItems.add(new RssItem(title, link, description, author, pubDate, guid));
        }

        return rssItems;
    }
}