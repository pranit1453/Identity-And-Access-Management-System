package com.pranit.accesshub.authorization.constant;

import lombok.Getter;

@Getter
public enum SortPermission {

    PERMISSION_NAME("permissionName");

    private final String field;

    SortPermission(String field) {
        this.field = field;
    }
}
