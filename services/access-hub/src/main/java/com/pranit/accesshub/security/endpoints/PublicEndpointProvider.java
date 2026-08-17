package com.pranit.accesshub.security.endpoints;

@FunctionalInterface
public interface PublicEndpointProvider {

    String[] publicEndpoints();
}
