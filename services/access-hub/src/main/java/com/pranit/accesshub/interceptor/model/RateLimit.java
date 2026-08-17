package com.pranit.accesshub.interceptor.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimit {

    private long windowStart;
    private int count;

    public RateLimit() {
        this.windowStart = System.currentTimeMillis();
        this.count = 0;
    }

}