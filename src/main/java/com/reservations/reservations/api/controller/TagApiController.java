package com.reservations.reservations.api.controller;






import com.reservations.reservations.dto.TagDTO;
import com.reservations.reservations.mapper.TagMapper;
import com.reservations.reservations.model.Tag;
import com.reservations.reservations.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")

public class TagApiController {


    @Autowired
    private TagService tagService;
    @Autowired
    private TagMapper tagMapper;



    @GetMapping
    public List<TagDTO> getAllTags() {
        return tagMapper.toDTOList( tagService.findAll() );
    }

    @PreAuthorize( "hasRole('ADMIN')" )
    @GetMapping("{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable Long id) {
        Optional<Tag> tag = tagService.find( id );
        return tag.map(value -> ResponseEntity.ok(tagMapper.toDTO( value )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize( "hasRole('ADMIN')" )
    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDTO) {
        Tag tag = new Tag();
        tag.setTag(tagDTO.getTag());
        tagService.save( tag );
        return ResponseEntity.ok( tagMapper.toDTO( tag ) );
    }

    @PreAuthorize( "hasRole('ADMIN')" )
    //update
    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(@PathVariable Long id, @RequestBody TagDTO tagDTO) {
        Optional<Tag> existing = tagService.find( id );
        if ( existing.isEmpty() ) {
            return ResponseEntity.notFound().build();
        }
        Tag tag = existing.get();
        tag.setTag(tagDTO.getTag());
        tagService.save( tag );
        return ResponseEntity.ok( tagMapper.toDTO( tag ) );
    }

    @PreAuthorize( "hasRole('ADMIN')" )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        Optional<Tag> tag = tagService.find(id);
        if (tag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }


}