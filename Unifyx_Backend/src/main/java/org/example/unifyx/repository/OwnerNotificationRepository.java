package org.example.unifyx.repository;

import org.example.unifyx.Model.OwnerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerNotificationRepository extends JpaRepository<OwnerNotification, Integer> {
    List<OwnerNotification> findByOwnerUidOrderByCreatedAtDesc(String ownerUid);

    long countByOwnerUidAndIsReadFalse(String ownerUid);

    void deleteByPostId(int postId);
}

