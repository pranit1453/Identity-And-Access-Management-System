package com.pranit.chat.agent.service.impl;

import com.pranit.chat.agent.service.TicketService;
import com.pranit.chat.client.service.AccessHubClient;
import com.pranit.chat.dto.TicketRequest;
import com.pranit.chat.dto.TicketResponse;
import com.pranit.chat.entities.entity.Ticket;
import com.pranit.chat.helper.Generate;
import com.pranit.chat.mapper.TicketMapper;
import com.pranit.chat.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final AccessHubClient accessHubClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TicketResponse createTicket(final TicketRequest request) {
        if (!accessHubClient.checkUserNameExists(request.username())) {
            return TicketResponse.builder()
                    .message("Please provide valid username exists on portal.")
                    .build();
        }
        if (ticketRepository.existsBySummary(request.summary())) {
            log.warn("Ticket already exists with summary {}", request.summary());
            return TicketResponse.builder()
                    .message("Ticket already exists.")
                    .build();
        }
        Ticket ticket = ticketMapper.mappedToTicket(request);
        ticket.setTicketId(Generate.ticketId());
        Ticket save = ticketRepository.save(ticket);
        log.info("Ticket created with id {}", save.getTicketId());
        return TicketResponse.builder()
                .ticketId(save.getTicketId())
                .message("Ticket created successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse fetchTicketById(final String ticketId) {
        final Ticket ticket = ticketRepository.findByTicketId(ticketId)
                .orElse(null);
        if (ticket == null) {
            log.warn("Ticket not found with id {}", ticketId);
            return TicketResponse.builder()
                    .message("Ticket not found with id " + ticketId)
                    .build();
        }
        return ticketMapper.mappedToTicketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> fetchTicketByUsername(final String username) {
        final List<Ticket> tickets = ticketRepository.findByUsername(username);
        if (tickets.isEmpty()) {
            log.info("No tickets found for username {}", username);
            return List.of(TicketResponse.builder()
                    .message("No ticket found for username: " + username)
                    .build());
        }
        return tickets.stream()
                .map(ticketMapper::mappedToTicketResponse)
                .toList();
    }
}
