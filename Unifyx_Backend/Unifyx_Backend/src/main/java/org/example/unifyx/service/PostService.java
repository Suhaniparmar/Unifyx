package org.example.unifyx.service;

import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getPostsByOwner(OwnerProfile owner) {
        return postRepository.findByOwner(owner);
    }
}
