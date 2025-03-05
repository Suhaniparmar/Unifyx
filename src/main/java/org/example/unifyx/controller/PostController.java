package org.example.unifyx.controller;

import org.example.unifyx.Model.Post;
import org.example.unifyx.service.PostService;
import org.example.unifyx.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;



    @PostMapping("/upload")
    public ResponseEntity<Post> createPost(
            @RequestParam("description") String description,
            @RequestParam("worker_category") String workerCategory,
            @RequestParam("site_address") String siteAddress,
            @RequestParam("site_location") String siteLocation,
            @RequestParam("duration") String duration,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        Post post = new Post();
        post.setDescription(description);
        post.setWorkerCategory(workerCategory);
        post.setSiteAddress(siteAddress);
        post.setLocation(siteLocation);
        post.setDuration(duration);

        // Pass post and images for processing
        Post savedPost = postService.createPostWithImages(post, images);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable int id) {
        Post post = postService.getPostById(id);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable int id) {
        boolean deleted = postService.deletePost(id);
        return deleted ? ResponseEntity.ok("Post deleted") : ResponseEntity.notFound().build();
    }
}
