package org.example.unifyx.controller;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Model.Post;
import org.example.unifyx.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner/home")
public class OwnerHomeController {

    @Autowired
    private PostService postService;

    @PostMapping("/newpost")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/posts/{ownerId}")
    public ResponseEntity<List<Post>> getPostsByOwner(@PathVariable int ownerId) {
        OwnerProfile owner = new OwnerProfile();
        owner.setOwnerId(ownerId);  // Assuming OwnerProfile has setOwnerId method
        List<Post> posts = postService.getPostsByOwner(owner);
        return ResponseEntity.ok(posts);
    }
}
