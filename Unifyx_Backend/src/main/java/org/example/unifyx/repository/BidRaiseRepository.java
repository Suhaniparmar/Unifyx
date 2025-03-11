package org.example.unifyx.repository;

import org.example.unifyx.Model.BidRaise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRaiseRepository extends JpaRepository<BidRaise, Integer> {
    List<BidRaise> findByPost_PostId(int postId);
}
