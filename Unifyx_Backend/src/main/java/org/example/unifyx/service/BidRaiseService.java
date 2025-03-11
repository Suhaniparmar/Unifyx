package org.example.unifyx.service;

import org.example.unifyx.Model.BidRaise;
import org.example.unifyx.Model.Post;
import org.example.unifyx.repository.BidRaiseRepository;
import org.example.unifyx.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BidRaiseService {

    @Autowired
    private BidRaiseRepository bidRaiseRepository;

    @Autowired
    private PostRepository postRepository;

    public BidRaise raiseBid(int postId, int senderId, String senderRole, double amount, String duration, int receiverId, String receiverRole) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new RuntimeException("Post not found with ID: " + postId);
        }

        BidRaise bidRaise = new BidRaise();
        bidRaise.setPost(postOptional.get());
        bidRaise.setSenderId(senderId);
        bidRaise.setSenderRole(senderRole);
        bidRaise.setAmount(amount);
        bidRaise.setDuration(duration);
        bidRaise.setReceiverId(receiverId);
        bidRaise.setReceiverRole(receiverRole);

        return bidRaiseRepository.save(bidRaise);
    }

    public List<BidRaise> getBidsByPost(int postId) {
        return bidRaiseRepository.findByPost_PostId(postId);
    }

    public boolean deleteBid(int bidRaiseId) {
        if (bidRaiseRepository.existsById(bidRaiseId)) {
            bidRaiseRepository.deleteById(bidRaiseId);
            return true;
        }
        return false;
    }
}
