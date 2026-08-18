package com.pranit.chat.service;

import com.pranit.chat.dto.TicketResponse;
import com.pranit.chat.dto.UpdateStatusRequest;
import com.pranit.chat.dto.UpdateSummaryRequest;
import com.pranit.chat.wrapper.PageResponse;

public interface TicketResolveService {

    PageResponse<TicketResponse> fetchTickets(int page, int size, String keyword, String sortBy, String sortDirection);

    Boolean updateStatus(String ticketId, UpdateStatusRequest request);

    Boolean solutionToSummary(String ticketId, UpdateSummaryRequest request);
}
