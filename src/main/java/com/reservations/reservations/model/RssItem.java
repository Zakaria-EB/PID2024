package com.reservations.reservations.model;

import lombok.Data;
import lombok.Getter;
@Data
@Getter
public class RssItem {
    // Getters uniquement (pas besoin de setters ici)
    private String title;
    private String link;
    private String description;
    private String author;
    private String pubDate;
    private String guid;

    public RssItem(String title, String link, String description, String author, String pubDate, String guid) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.pubDate = pubDate;
        this.guid = guid;
    }

}