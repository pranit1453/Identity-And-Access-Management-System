package com.pranit.chat.service.impl;

import com.pranit.chat.dto.TicketResponse;
import com.pranit.chat.dto.UpdateStatusRequest;
import com.pranit.chat.dto.UpdateSummaryRequest;
import com.pranit.chat.entities.entity.Ticket;
import com.pranit.chat.mapper.TicketMapper;
import com.pranit.chat.repository.TicketRepository;
import com.pranit.chat.service.TicketResolveService;
import com.pranit.chat.ticket.TicketNotFoundException;
import com.pranit.chat.ticket.TicketSpecification;
import com.pranit.chat.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketResolveServiceImpl implements TicketResolveService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TicketResponse> fetchTickets(int page, int size, String keyword, String sortBy, String sortDirection) {
        final Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        final Pageable pageable = PageRequest.of(page, size, sort);
        final Specification<Ticket> specification = TicketSpecification.searchKeyword(keyword);
        final Page<Ticket> pages = ticketRepository.findAll(specification, pageable);
        final List<TicketResponse> content = pages.getContent()
                .stream()
                .map(ticketMapper::mappedToTicketResponse)
                .toList();
        log.debug("Fetch {} tickets", pages.getTotalElements());
        return PageResponse.<TicketResponse>builder()
                .content(content)
                .page(pages.getNumber())
                .size(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .last(pages.isLast())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateStatus(String ticketId, UpdateStatusRequest request) {
        final Ticket ticket = validateAndFetchTicketByTicketId(ticketId);
        ticket.setStatus(request.status());
        return true;
    }

    private Ticket validateAndFetchTicketByTicketId(String ticketId) {
        return ticketRepository.findByTicketId(ticketId)
                .orElseThrow(() -> {
                    log.warn("Ticket with id {} not found", ticketId);
                    return new TicketNotFoundException("Ticket not found");
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean solutionToSummary(String ticketId, UpdateSummaryRequest request) {
        final Ticket ticket = validateAndFetchTicketByTicketId(ticketId);
        final String existingSummary = ticket.getSummary();
        final String solution = request.solution();
        final String updatedSummary;
        if (existingSummary == null || existingSummary.isBlank()) updatedSummary = solution;
        else updatedSummary = existingSummary + "\n\nSolution: " + solution;
        ticket.setSummary(updatedSummary);
        return true;
    }
}
