package com.virul.medisure.service;

import com.virul.medisure.dto.TicketRequest;
import com.virul.medisure.dto.TicketResponse;
import com.virul.medisure.dto.TicketCommentResponse;
import com.virul.medisure.model.Ticket;
import com.virul.medisure.model.TicketComment;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.TicketRepository;
import com.virul.medisure.repository.TicketCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    
    public TicketResponse createTicket(TicketRequest request, User user) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setUser(user);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        Ticket savedTicket = ticketRepository.save(ticket);
        return new TicketResponse(savedTicket);
    }
    
    public List<TicketResponse> getUserTickets(User user) {
        List<Ticket> tickets = ticketRepository.findByUserOrderByCreatedAtDesc(user);
        return tickets.stream()
                .map(ticket -> {
                    TicketResponse response = new TicketResponse(ticket);
                    response.setComments(getTicketComments(ticket.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    public List<TicketResponse> getAssignedTickets(User user) {
        List<Ticket> tickets = ticketRepository.findByAssignedToOrderByCreatedAtDesc(user);
        return tickets.stream()
                .map(ticket -> {
                    TicketResponse response = new TicketResponse(ticket);
                    response.setComments(getTicketComments(ticket.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    public List<TicketResponse> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(ticket -> {
                    TicketResponse response = new TicketResponse(ticket);
                    response.setComments(getTicketComments(ticket.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    public TicketResponse assignTicket(Long ticketId, User assignedTo) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setAssignedTo(assignedTo);
        ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        Ticket savedTicket = ticketRepository.save(ticket);
        TicketResponse response = new TicketResponse(savedTicket);
        response.setComments(getTicketComments(ticketId));
        return response;
    }
    
    public TicketResponse updateTicketStatus(Long ticketId, Ticket.TicketStatus status, User user) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (status == Ticket.TicketStatus.RESOLVED || status == Ticket.TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        
        Ticket savedTicket = ticketRepository.save(ticket);
        TicketResponse response = new TicketResponse(savedTicket);
        response.setComments(getTicketComments(ticketId));
        return response;
    }
    
    public TicketCommentResponse addComment(Long ticketId, String comment, User user, boolean isInternal) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        TicketComment ticketComment = new TicketComment();
        ticketComment.setTicket(ticket);
        ticketComment.setUser(user);
        ticketComment.setComment(comment);
        ticketComment.setInternal(isInternal);
        ticketComment.setCreatedAt(LocalDateTime.now());
        
        TicketComment savedComment = ticketCommentRepository.save(ticketComment);
        return new TicketCommentResponse(savedComment);
    }
    
    public List<TicketCommentResponse> getTicketComments(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        List<TicketComment> comments = ticketCommentRepository.findByTicketAndIsInternalOrderByCreatedAtAsc(ticket, false);
        return comments.stream()
                .map(TicketCommentResponse::new)
                .collect(Collectors.toList());
    }
    
    public long getTicketCountByStatus(Ticket.TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }
    
    public long getTicketCountByAssignedUserAndStatus(User user, Ticket.TicketStatus status) {
        return ticketRepository.countByAssignedToAndStatus(user, status);
    }
}
