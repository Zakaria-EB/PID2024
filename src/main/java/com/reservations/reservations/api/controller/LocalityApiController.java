package com.reservations.reservations.api.controller;


import com.reservations.reservations.dto.LocalityDTO;
import com.reservations.reservations.mapper.LocationShowMapper;
import com.reservations.reservations.model.Locality;
import com.reservations.reservations.service.LocalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/localities")
public class LocalityApiController {


    @Autowired
    private LocalityService localityService;
    @Autowired
    private LocationShowMapper mapper;


    @GetMapping
    public List<     LocalityDTO> getAll() {
        return localityService.getAll()
                .stream()
                .map( mapper::mapLocalityToLocalityDTO)
                .collect( Collectors.toList() );
    }


    @GetMapping("/{id}")
    public LocalityDTO get(@PathVariable String id) {
        Locality locality = localityService.get( id );
        return mapper.mapLocalityToLocalityDTO( locality );
    }





}
