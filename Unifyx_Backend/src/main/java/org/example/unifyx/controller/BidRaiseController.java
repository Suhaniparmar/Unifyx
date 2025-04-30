package org.example.unifyx.controller;

import org.example.unifyx.Model.BidRaise;
import org.example.unifyx.Model.Post;
import org.example.unifyx.repository.PostRepository;
import org.example.unifyx.service.BidRaiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bids")
public class BidRaiseController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private BidRaiseService bidRaiseService;

    @PostMapping("/raise")
    public ResponseEntity<BidRaise> raiseBid(
            @RequestParam Integer senderId,
            @RequestParam Integer postId,
            @RequestParam Double amount,
            @RequestParam String duration) {

        BidRaise bid = bidRaiseService.placeBid(senderId, postId, amount, duration);
        return ResponseEntity.ok(bid);
    }

    // Owners get all bids for a particular post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<BidRaise>> getBidsForPost(@PathVariable int postId) {
        List<BidRaise> bids = bidRaiseService.getBidsForPost(postId);
        if (bids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(bids);
    }
}



