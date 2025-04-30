package org.example.unifyx.service;

import org.example.unifyx.Model.BidRaise;
import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.Users;
import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.repository.BidRaiseRepository;
import org.example.unifyx.repository.PostRepository;
import org.example.unifyx.repository.UserRepository;
import org.example.unifyx.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BidRaiseService {

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private BidRaiseRepository bidRaiseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public BidRaise placeBid(int senderId, int postId, Double amount, String duration) {
        WorkerProfile sender = workerProfileRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Worker not found with ID: " + senderId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        BidRaise bid = new BidRaise(amount, duration, sender, post);
        return bidRaiseRepository.save(bid);
    }

    public List<BidRaise> getBidsForPost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return bidRaiseRepository.findByPost(post);
    }
}
