package org.example.unifyx.repository;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUser_Uid(String uid);

    List<Post> findBySiteLocation(String siteLocation);



}
