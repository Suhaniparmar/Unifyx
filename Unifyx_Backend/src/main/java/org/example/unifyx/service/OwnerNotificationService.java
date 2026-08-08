package org.example.unifyx.service;

import org.example.unifyx.Model.OwnerNotification;
import org.example.unifyx.repository.OwnerNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OwnerNotificationService {

    @Autowired
    private OwnerNotificationRepository ownerNotificationRepository;

    public OwnerNotification createQuoteNotification(String ownerUid, int postId, int quoteId, String senderName, boolean isUpdate) {
        String title = isUpdate ? "Quote updated" : "New quote received";
        String message = senderName + (isUpdate ? " updated quote" : " sent a quote") + " on your post #" + postId;

        OwnerNotification notification = new OwnerNotification(ownerUid, postId, quoteId, title, message);
        return ownerNotificationRepository.save(notification);
    }

    public List<OwnerNotification> getNotificationsForOwner(String ownerUid) {
        return ownerNotificationRepository.findByOwnerUidOrderByCreatedAtDesc(ownerUid);
    }

    public long getUnreadCountForOwner(String ownerUid) {
        return ownerNotificationRepository.countByOwnerUidAndIsReadFalse(ownerUid);
    }

    public OwnerNotification markAsRead(int notificationId) {
        OwnerNotification notification = ownerNotificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notification.setRead(true);
        return ownerNotificationRepository.save(notification);
    }
}

