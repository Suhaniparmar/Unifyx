package org.example.unifyx.controller;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Model.Post;
import org.example.unifyx.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/owner/home")
public class OwnerHomeController {

    @Autowired
    private PostService postService;

    // Create a new post with Cloudinary image upload
    @PostMapping("/newpost")
    public ResponseEntity<String> createPost(
            @RequestPart("post") Post post,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        postService.createPostWithImages(post, images);
        return ResponseEntity.ok("Post created successfully!");
    }

//    // Get all posts by a specific owner
//    @GetMapping("/posts/{ownerId}")
//    public ResponseEntity<List<Post>> getPostsByOwner(@PathVariable OwnerProfile ownerId) {
//        List<Post> posts = postService.getPostsByOwner(ownerId);
//        return ResponseEntity.ok(posts);
//    }
}
