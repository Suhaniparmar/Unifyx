package org.example.unifyx.repository;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByOwner(OwnerProfile owner);
}
