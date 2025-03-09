package org.example.unifyx.service;

import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.Users;
import org.example.unifyx.cloudinary.CloudinaryService;
import org.example.unifyx.repository.PostRepository;
import org.example.unifyx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PostRepository postRepository;

    public Post createPostWithImages(Post post, List<MultipartFile> images, String ownerUid) {
        try {
            Users user = userRepository.findByUid(ownerUid).orElseThrow(() -> new RuntimeException("Owner not found"));
            post.setUser(user);
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

    @Transactional
    public List<Post> getPostsByUserUid(String uid) {
        return postRepository.findByUser_Uid(uid);
    }

    public boolean deletePost(int postId) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }
}
