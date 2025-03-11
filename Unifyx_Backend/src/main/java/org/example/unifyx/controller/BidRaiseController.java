package org.example.unifyx.controller;

import org.example.unifyx.Model.BidRaise;
import org.example.unifyx.service.BidRaiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
public class BidRaiseController {

    @Autowired
    private BidRaiseService bidRaiseService;

    @PostMapping("/raise")
    public ResponseEntity<BidRaise> raiseBid(@RequestBody BidRaise bidRaise) {
        BidRaise savedBid = bidRaiseService.raiseBid(
                bidRaise.getPost().getPostId(),
                bidRaise.getSenderId(),
                bidRaise.getSenderRole(),
                bidRaise.getAmount(),
                bidRaise.getDuration(),
                bidRaise.getReceiverId(),
                bidRaise.getReceiverRole()
        );
        return ResponseEntity.ok(savedBid);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<BidRaise>> getBidsByPost(@PathVariable int postId) {
        List<BidRaise> bids = bidRaiseService.getBidsByPost(postId);
        return ResponseEntity.ok(bids);
    }

    @DeleteMapping("/{bidRaiseId}")
    public ResponseEntity<String> deleteBid(@PathVariable int bidRaiseId) {
        boolean deleted = bidRaiseService.deleteBid(bidRaiseId);
        return deleted ? ResponseEntity.ok("Bid deleted") : ResponseEntity.notFound().build();
    }
}
