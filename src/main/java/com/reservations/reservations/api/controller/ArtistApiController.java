package com.reservations.reservations.api.controller;






import com.reservations.reservations.dto.ArtistDTO;
import com.reservations.reservations.dto.ArtistTypeDTO;
import com.reservations.reservations.mapper.ArtistMapper;
import com.reservations.reservations.model.Artist;
import com.reservations.reservations.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")

public class ArtistApiController {

    @Autowired
    private ArtistService artistService;
    @Autowired
    private ArtistMapper artistMapper;
    // GET /api/artists
    @GetMapping("/artists")
    public List<ArtistDTO> listAll() {
        return artistService.getAllArtists().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    // GET /api/artists/{id}
    @GetMapping("/artists/{id}")
    public ResponseEntity<ArtistTypeDTO> getById(@PathVariable Long id) {
        Artist a = artistService.getArtist(id);
        if (a == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(artistMapper.toArtistTypeDTO(a));
    }

    @PostMapping ("/artists")
    public ResponseEntity<ArtistDTO> create(@Valid @RequestBody ArtistDTO artistDTO) {
        Artist a = artistMapper.toEntity(artistDTO);
        artistService.addArtist(a);
        return ResponseEntity.ok(artistMapper.toDTO(a));

    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<ArtistDTO> update(@Valid @PathVariable Long id, @RequestBody ArtistDTO artistDTO ) {
        Artist existing = artistService.getArtist(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setFirstname( artistDTO.getFirstname() );
        existing.setLastname( artistDTO.getLastname() );
        artistService.updateArtist( id, existing );

        return ResponseEntity.ok(artistMapper.toDTO(existing));
    }
    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Artist existing = artistService.getArtist(id);
        if (existing == null) return ResponseEntity.notFound().build();

        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();

    }


}
