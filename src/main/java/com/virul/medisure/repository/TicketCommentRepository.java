package com.virul.medisure.repository;

import com.virul.medisure.model.Ticket;
import com.virul.medisure.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    
    List<TicketComment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
    
    List<TicketComment> findByTicketAndIsInternalOrderByCreatedAtAsc(Ticket ticket, boolean isInternal);
}
