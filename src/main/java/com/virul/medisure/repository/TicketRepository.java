package com.virul.medisure.repository;

import com.virul.medisure.model.Ticket;
import com.virul.medisure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByUser(User user);
    
    List<Ticket> findByAssignedTo(User assignedTo);
    
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE t.user = :user ORDER BY t.createdAt DESC")
    List<Ticket> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT t FROM Ticket t WHERE t.assignedTo = :assignedTo ORDER BY t.createdAt DESC")
    List<Ticket> findByAssignedToOrderByCreatedAtDesc(User assignedTo);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(Ticket.TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo = :assignedTo AND t.status = :status")
    long countByAssignedToAndStatus(User assignedTo, Ticket.TicketStatus status);
}
