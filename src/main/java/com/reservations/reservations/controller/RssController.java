package com.reservations.reservations.controller;

import com.reservations.reservations.model.RssItem;
import com.reservations.reservations.service.RssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RssController {

    @Autowired
    private RssService rssService;

    @GetMapping(value = "/rss.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getRssFeed() {
        List<RssItem> items = rssService.getUpcomingShows();

        StringBuilder rss = new StringBuilder();
        rss.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        rss.append("<rss version=\"2.0\">\n");
        rss.append("<channel>\n");
        rss.append("<title>Spectacles à venir</title>\n");
        rss.append("<link>https://tonsite.com</link>\n");
        rss.append("<description>Les spectacles à ne pas manquer</description>\n");

        for (RssItem item : items) {
            rss.append("<item>\n");
            rss.append("<title>").append(item.getTitle()).append("</title>\n");
            rss.append("<link>").append(item.getLink()).append("</link>\n");
            rss.append("<description><![CDATA[").append(item.getDescription()).append("]]></description>\n");
            rss.append("<author>").append(item.getAuthor()).append("</author>\n");
            rss.append("<pubDate>").append(item.getPubDate()).append("</pubDate>\n");
            rss.append("<guid>").append(item.getGuid()).append("</guid>\n");
            rss.append("</item>\n");
        }

        rss.append("</channel>\n</rss>");

        return ResponseEntity.ok(rss.toString());
    }
}