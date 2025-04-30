package org.example.unifyx.controller;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.repository.ContractorProfileRepository;
import org.example.unifyx.repository.WorkerProfileRepository;
import org.example.unifyx.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/search")
public class SearchController {


    @Autowired
    private SearchService searchService;

    @GetMapping("/by-location")
    public ResponseEntity<List<Object>> searchByLocation(@RequestParam("location") String location) {
        List<WorkerProfile> workers = searchService.getWorkersByLocation(location);
        List<ContractorProfile> contractors = searchService.getContractorsByLocation(location);

        List<Object> result = new ArrayList<>();
        result.addAll(workers);
        result.addAll(contractors);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList("No results found!"));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/by-location")
    public List<Post> searchPostsByLocation(@RequestParam("location") String location) {
        return searchService.searchPostsByLocation(location);
    }
}

