package org.example.unifyx.controller;

import org.example.unifyx.Model.Post;
import org.example.unifyx.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        String ownerUid = post.getUser().getUid();
        try {
            postService.createPostWithImages(post, images, ownerUid);
            return ResponseEntity.ok("Post created successfully!");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Cloudinary is not configured. Set credentials and restart backend.");
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Failed to upload image to Cloudinary")) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Image upload failed. Please try again later.");
            }
            throw ex;
        }
    }

//    // Get all posts by a specific owner
//    @GetMapping("/posts/{ownerId}")
//    public ResponseEntity<List<Post>> getPostsByOwner(@PathVariable OwnerProfile ownerId) {
//        List<Post> posts = postService.getPostsByOwner(ownerId);
//        return ResponseEntity.ok(posts);
//    }
}
