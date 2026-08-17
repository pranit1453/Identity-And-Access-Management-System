package com.pranit.accesshub.interceptor;

import com.pranit.accesshub.interceptor.model.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, RateLimit> clients = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String clientIp = request.getRemoteAddr();
        RateLimit rateLimit = clients.computeIfAbsent(clientIp, key -> new RateLimit());
        synchronized (rateLimit) {
            long now = System.currentTimeMillis();
            // New 60-second window
            if (now - rateLimit.getWindowStart() >= WINDOW_MS) {
                rateLimit.setWindowStart(now);
                rateLimit.setCount(0);
            }
            // Limit exceeded
            if (rateLimit.getCount() >= MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                try {
                    response.getWriter().write(
                            """
                                    {
                                        "status": 429,
                                        "message": "Too many requests. Please try again later."
                                    }
                                    """
                    );
                } catch (Exception e) {
                    log.error("Error while writing response: ", e);
                }
                return false;
            }
            // Count current request
            rateLimit.setCount(rateLimit.getCount() + 1);
            return true;
        }
    }
}
