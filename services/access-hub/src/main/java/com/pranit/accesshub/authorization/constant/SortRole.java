package com.pranit.accesshub.authorization.constant;

import lombok.Getter;

@Getter
public enum SortRole {

    ROLE_NAME("roleName");

    private final String field;

    SortRole(String field) {
        this.field = field;
    }

}
