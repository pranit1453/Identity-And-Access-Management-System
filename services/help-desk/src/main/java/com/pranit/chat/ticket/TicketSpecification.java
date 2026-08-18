package com.pranit.chat.ticket;

import com.pranit.chat.entities.entity.Ticket;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TicketSpecification {

    private TicketSpecification() {
    }

    public static Specification<Ticket> searchKeyword(final String keyword) {
        return ((root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();
            final String likePattern = "%" + keyword.toLowerCase() + "%";
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("username")), likePattern));
            return cb.or(predicates.toArray(new Predicate[0]));
        });
    }
}
