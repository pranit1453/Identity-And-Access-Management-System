package com.pranit.accesshub.authorization.specification;

import com.pranit.accesshub.entities.entity.Permission;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PermissionSpecification {

    private PermissionSpecification() {
    }

    public static Specification<Permission> searchKeyword(final String keyword) {
        return ((root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();
            final String likePattern = "%" + keyword.toLowerCase() + "%";
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("permissionName")), likePattern));

            return cb.or(predicates.toArray(new Predicate[0]));
        });
    }

}
