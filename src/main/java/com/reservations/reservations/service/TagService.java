package com.reservations.reservations.service;

import com.reservations.reservations.model.Show;
import com.reservations.reservations.model.Tag;
import com.reservations.reservations.repository.ShowRepository;
import com.reservations.reservations.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final ShowRepository showRepository;

    public TagService(TagRepository tagRepository, ShowRepository showRepository) {
        this.tagRepository = tagRepository;
        this.showRepository = showRepository;
    }

    @Transactional
    public void addTagToShow(String tagLabel, Show show) {
        // 🔍 Recherche du tag existant
        Optional<Tag> optionalTag = tagRepository.findByTag(tagLabel.trim());

        Tag tag;

        if (optionalTag.isPresent()) {
            tag = optionalTag.get();
        } else {
            // ✅ Création du tag s’il n’existe pas
            tag = new Tag();
            tag.setTag(tagLabel.trim());
            tag = tagRepository.save(tag);
        }

        // 🔗 Ajout au show s’il n’est pas déjà présent
        if (!show.getTags().contains(tag)) {
            show.getTags().add(tag);
            showRepository.save(show); // mise à jour côté "owning side"
        }
    }
}

