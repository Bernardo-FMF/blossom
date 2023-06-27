package org.blossom.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

public class Config {
    private static final String headerValuePrefix = "Bearer";

    public String getHeaderName() {
        return HttpHeaders.AUTHORIZATION;
    }

    public boolean validateHeaderValuePrefix(String headerPrefix) {
        return headerPrefix != null && headerPrefix.startsWith(headerValuePrefix);
    }

    public String extractAuthorizationHeaderValue(HttpHeaders headers) {
        String authHeader = Objects.requireNonNull(headers.get(getHeaderName())).get(0);
        if (validateHeaderValuePrefix(authHeader)) {
            return authHeader.substring(7);
        }
        return null;
    }
}
