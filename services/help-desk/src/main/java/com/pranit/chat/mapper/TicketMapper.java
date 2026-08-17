package com.pranit.chat.mapper;

import com.pranit.chat.dto.TicketRequest;
import com.pranit.chat.dto.TicketResponse;
import com.pranit.chat.entities.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticketId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Ticket mappedToTicket(TicketRequest ticket);

    TicketResponse mappedToTicketResponse(Ticket ticket);
}
