package org.example.unifyx.controller;

import org.example.unifyx.Model.Post;
import org.example.unifyx.service.ContractorHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/contractor/home")
public class ContractorHomeController {
    @Autowired
    private ContractorHomeService contractorHomeService;

    /**
     * Get posts that match the worker's categories
     */
    @GetMapping("/{contractorId}/posts")
    public ResponseEntity<List<Post>> getWorkerHomePosts(@PathVariable int contractorId) {
        List<Post> matchingPosts = contractorHomeService.getMatchingPostsForContractor(contractorId);

        if (matchingPosts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(matchingPosts);
    }
}
