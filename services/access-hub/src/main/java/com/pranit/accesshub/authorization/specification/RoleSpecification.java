package com.pranit.accesshub.authorization.specification;

import com.pranit.accesshub.entities.entity.Role;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class RoleSpecification {

    private RoleSpecification() {
    }

    public static Specification<Role> searchKeyword(final String keyword) {
        return ((root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();
            final String likePattern = "%" + keyword.toLowerCase() + "%";
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("roleName")), likePattern));

            return cb.or(predicates.toArray(new Predicate[0]));
        });
    }

}
