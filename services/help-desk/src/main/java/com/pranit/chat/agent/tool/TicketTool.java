package com.pranit.chat.agent.tool;

import com.pranit.chat.agent.service.TicketService;
import com.pranit.chat.dto.TicketRequest;
import com.pranit.chat.dto.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketTool {

    private final TicketService ticketService;

    @Tool(
            name = "create_new_ticket",
            description = """
                    Creates a new ticket.
                    Use this tool when the user wants to report a new issue, bug,
                    service request, or technical problem.
                    The ticket should include a clear summary and all available details.
                    Returns the generated ticket ID after successful creation.
                    """
    )
    @Transactional
    public TicketResponse createNewTicket(
            @ToolParam(
                    description = """
                            Details of the ticket to create, including the summary,
                            description, priority, category, and any other required fields.
                            """) TicketRequest ticket) {
        return ticketService.createTicket(ticket);
    }

    @Tool(
            name = "get_ticket_by_ticket_id",
            description = """
                    Retrieves the details of an existing help desk ticket using its ticket ID.
                    Use this tool when the user wants to check the status, details, or information
                    about a previously created ticket.
                    """
    )
    @Transactional
    public TicketResponse getTicketByTicketId(
            @ToolParam(
                    description = """
                            The unique ticket ID generated when the ticket was created.
                            Example: TKT-20260806-483921
                            """
            ) String ticketId) {
        return ticketService.fetchTicketById(ticketId);
    }

}
