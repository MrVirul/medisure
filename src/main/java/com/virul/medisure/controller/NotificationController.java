package com.virul.medisure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for notification-related API endpoints
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    /**
     * Get unread notification count
     * Returns the count of unread notifications for the current user
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", 0);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get notifications list
     * Returns an empty list of notifications (placeholder)
     */
    @GetMapping
    public ResponseEntity<String> getNotifications() {
        // Return empty notification list as HTML fragment
        String html = """
            <div class="p-4 text-center text-slate-500">
                <p class="text-sm">No new notifications</p>
            </div>
            """;
        return ResponseEntity.ok(html);
    }
}


