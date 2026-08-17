package com.pranit.chat.agent.service;

import com.pranit.chat.dto.TicketRequest;
import com.pranit.chat.dto.TicketResponse;

import java.util.List;

public interface TicketService {

    TicketResponse createTicket(TicketRequest request);

    TicketResponse fetchTicketById(String ticketId);

    List<TicketResponse> fetchTicketByUsername(String username);
}
