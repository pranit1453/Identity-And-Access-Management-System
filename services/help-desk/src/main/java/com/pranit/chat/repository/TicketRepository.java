package com.pranit.chat.repository;

import com.pranit.chat.entities.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    boolean existsBySummary(String summary);

    Optional<Ticket> findByTicketId(String ticketId);

    List<Ticket> findByUsername(String username);
}
