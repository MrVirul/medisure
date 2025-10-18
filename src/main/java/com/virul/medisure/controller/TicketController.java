package com.virul.medisure.controller;

import com.virul.medisure.dto.*;
import com.virul.medisure.model.Ticket;
import com.virul.medisure.model.User;
import com.virul.medisure.service.TicketService;
import com.virul.medisure.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    private final UserService userService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'POLICY_HOLDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@RequestBody TicketRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            TicketResponse ticket = ticketService.createTicket(request, user);
            return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/my-tickets")
    @PreAuthorize("hasAnyRole('USER', 'POLICY_HOLDER', 'ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<TicketResponse>>> getMyTickets(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            java.util.List<TicketResponse> tickets = ticketService.getUserTickets(user);
            return ResponseEntity.ok(ApiResponse.success(tickets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('CUSTOMER_SUPPORT_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<TicketResponse>>> getAssignedTickets(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            java.util.List<TicketResponse> tickets = ticketService.getAssignedTickets(user);
            return ResponseEntity.ok(ApiResponse.success(tickets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATION_MANAGER', 'CUSTOMER_SUPPORT_OFFICER')")
    public ResponseEntity<ApiResponse<java.util.List<TicketResponse>>> getAllTickets() {
        try {
            java.util.List<TicketResponse> tickets = ticketService.getAllTickets();
            return ResponseEntity.ok(ApiResponse.success(tickets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('CUSTOMER_SUPPORT_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TicketResponse>> assignTicket(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long assignedToId = request.get("assignedToId");
            User assignedTo = userService.getUserById(assignedToId);
            TicketResponse ticket = ticketService.assignTicket(id, assignedTo);
            return ResponseEntity.ok(ApiResponse.success("Ticket assigned successfully", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('CUSTOMER_SUPPORT_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicketStatus(@PathVariable Long id, @RequestBody Map<String, String> request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Ticket.TicketStatus status = Ticket.TicketStatus.valueOf(request.get("status"));
            TicketResponse ticket = ticketService.updateTicketStatus(id, status, user);
            return ResponseEntity.ok(ApiResponse.success("Ticket status updated successfully", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'POLICY_HOLDER', 'CUSTOMER_SUPPORT_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TicketCommentResponse>> addComment(@PathVariable Long id, @RequestBody Map<String, String> request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            String comment = request.get("comment");
            boolean isInternal = Boolean.parseBoolean(request.getOrDefault("isInternal", "false"));
            TicketCommentResponse commentResponse = ticketService.addComment(id, comment, user, isInternal);
            return ResponseEntity.ok(ApiResponse.success("Comment added successfully", commentResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'POLICY_HOLDER', 'CUSTOMER_SUPPORT_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<TicketCommentResponse>>> getTicketComments(@PathVariable Long id) {
        try {
            java.util.List<TicketCommentResponse> comments = ticketService.getTicketComments(id);
            return ResponseEntity.ok(ApiResponse.success(comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATION_MANAGER', 'CUSTOMER_SUPPORT_OFFICER')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTicketStats() {
        try {
            Map<String, Long> stats = Map.of(
                "open", ticketService.getTicketCountByStatus(Ticket.TicketStatus.OPEN),
                "inProgress", ticketService.getTicketCountByStatus(Ticket.TicketStatus.IN_PROGRESS),
                "resolved", ticketService.getTicketCountByStatus(Ticket.TicketStatus.RESOLVED),
                "closed", ticketService.getTicketCountByStatus(Ticket.TicketStatus.CLOSED)
            );
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
