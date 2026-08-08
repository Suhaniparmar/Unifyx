package org.example.unifyx.controller;

import org.example.unifyx.Model.OwnerNotification;
import org.example.unifyx.service.OwnerNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class OwnerNotificationController {

    @Autowired
    private OwnerNotificationService ownerNotificationService;

    @GetMapping("/owners/{ownerUid}")
    public ResponseEntity<List<OwnerNotification>> getOwnerNotifications(@PathVariable String ownerUid) {
        return ResponseEntity.ok(ownerNotificationService.getNotificationsForOwner(ownerUid));
    }

    @GetMapping("/owners/{ownerUid}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String ownerUid) {
        long unreadCount = ownerNotificationService.getUnreadCountForOwner(ownerUid);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<OwnerNotification> markNotificationAsRead(@PathVariable int notificationId) {
        return ResponseEntity.ok(ownerNotificationService.markAsRead(notificationId));
    }
}

