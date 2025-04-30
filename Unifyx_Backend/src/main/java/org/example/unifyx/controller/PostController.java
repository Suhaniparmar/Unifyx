package org.example.unifyx.controller;

import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.Users;
import org.example.unifyx.repository.PostRepository;
import org.example.unifyx.repository.UserRepository;
import org.example.unifyx.service.PostService;
import org.example.unifyx.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;



    @PostMapping("/upload")
    public ResponseEntity<Post> createPost(
            @RequestParam("uid") String uid,
            @RequestParam("description") String description,
            @RequestParam("worker_category") String workerCategory,
            @RequestParam("site_address") String siteAddress,
            @RequestParam("site_location") String siteLocation,
            @RequestParam("duration") String duration,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        Users user = userRepository.findUserByUid(uid);

        Post post = new Post();
        post.setDescription(description);
        post.setWorkerCategory(workerCategory);
        post.setSiteAddress(siteAddress);
        post.setLocation(siteLocation);
        post.setDuration(duration);
        post.setUser(user);

        // Pass post and images for processing
        Post savedPost = postService.createPostWithImages(post, images, uid);
        return ResponseEntity.ok(savedPost);
    }


    @GetMapping("/all")
    public List<Post> getAllPosts() {

        List<Post> posts = postRepository.findAll();

        // Debugging: Check if images exist in the response
        for (Post post : posts) {
            System.out.println("Post ID: " + post.getPostId());
            System.out.println("Photos: " + post.getPhotos()); // Check if this prints URLs
        }

        return posts;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable int id) {
        Optional<Post> post = postService.getPostById(id);

        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value="/owner/{uid}", produces = "application/json")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable String uid) {
        List<Post> posts = postService.getPostsByUserUid(uid);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable int id) {
        boolean deleted = postService.deletePost(id);
        return deleted ? ResponseEntity.ok("Post deleted") : ResponseEntity.notFound().build();
    }
}
