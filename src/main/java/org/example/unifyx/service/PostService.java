package org.example.unifyx.service;

import org.example.unifyx.Model.Post;
import org.example.unifyx.cloudinary.CloudinaryService;
import org.example.unifyx.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PostRepository postRepository;

    public Post createPostWithImages(Post post, List<MultipartFile> images) {
        try {
            List<String> imageUrls = new ArrayList<>();

            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    String imageUrl = cloudinaryService.uploadImage(image);
                    imageUrls.add(imageUrl);
                }
            }
            post.setPhotos(imageUrls);

            return postRepository.save(post);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload images");
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public boolean deletePost(int postId) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }
}
