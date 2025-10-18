package com.virul.medisure.dto;

import com.virul.medisure.model.TicketComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommentResponse {
    
    private Long id;
    private String comment;
    private String userName;
    private String userRole;
    private LocalDateTime createdAt;
    private boolean isInternal;
    
    public TicketCommentResponse(TicketComment comment) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.userName = comment.getUser().getFullName();
        this.userRole = comment.getUser().getRole().name();
        this.createdAt = comment.getCreatedAt();
        this.isInternal = comment.isInternal();
    }
}
