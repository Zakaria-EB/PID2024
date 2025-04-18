package com.reservations.reservations.service;

import com.reservations.reservations.model.Show;
import com.reservations.reservations.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {
    @Autowired
    private ShowRepository repository;

    public List<Show> getAllShows() {
        return (List<Show>) repository.findAll();
    }

    public Optional<Show> getShowById(Long id) {
        return repository.findById(id);
    }

    public void addShow(Show show) {
        repository.save(show);
    }

    public void updateShow(Long id, Show updatedShow) {
        repository.save(updatedShow);
    }

    public void deleteShow(Long id) {
        repository.deleteById(id);
    }

    public List<Show> searchByTagKeyword(String keyword) {
        return repository.findByTagKeyword(keyword);
    }

    public List<Show> excludeByTag(String tag) {
        return repository.findByTagNot(tag);
    }
}