package com.pranit.chat.controller;

import com.pranit.chat.dto.TicketResponse;
import com.pranit.chat.dto.UpdateStatusRequest;
import com.pranit.chat.dto.UpdateSummaryRequest;
import com.pranit.chat.service.TicketResolveService;
import com.pranit.chat.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
@Validated
public class TicketController {

    private final TicketResolveService ticketResolveService;

    @GetMapping("/all")
    public ResponseEntity<PageResponse<TicketResponse>> fetchRoles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "username") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        final PageResponse<TicketResponse> responses = ticketResolveService.fetchTickets(page, size, keyword, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PatchMapping("/status/{ticketId}")
    public ResponseEntity<Boolean> updateStatus(@PathVariable String ticketId, @RequestBody UpdateStatusRequest request) {
        final Boolean response = ticketResolveService.updateStatus(ticketId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/solution/{ticketId}")
    public ResponseEntity<Boolean> solutionToSummary(@PathVariable String ticketId, @RequestBody UpdateSummaryRequest request) {
        final Boolean response = ticketResolveService.solutionToSummary(ticketId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
