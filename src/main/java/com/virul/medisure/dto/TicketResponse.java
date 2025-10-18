package com.virul.medisure.dto;

import com.virul.medisure.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    
    private Long id;
    private String title;
    private String description;
    private Ticket.TicketStatus status;
    private Ticket.TicketPriority priority;
    private String userName;
    private String userEmail;
    private String assignedToName;
    private String assignedToEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private List<TicketCommentResponse> comments;
    
    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.description = ticket.getDescription();
        this.status = ticket.getStatus();
        this.priority = ticket.getPriority();
        this.userName = ticket.getUser().getFullName();
        this.userEmail = ticket.getUser().getEmail();
        this.assignedToName = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getFullName() : null;
        this.assignedToEmail = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getEmail() : null;
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.resolvedAt = ticket.getResolvedAt();
    }
}
