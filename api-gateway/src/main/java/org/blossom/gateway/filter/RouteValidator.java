package org.blossom.gateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final Map<String, List<HttpMethod>> openApiEndpoints = new HashMap<>();

    static {
        openApiEndpoints.put("/api/v1/auth/register", Collections.singletonList(HttpMethod.POST));
        openApiEndpoints.put("/api/v1/auth/login", Collections.singletonList(HttpMethod.POST));
        openApiEndpoints.put("/api/v1/auth/validate", Collections.singletonList(HttpMethod.GET));
        openApiEndpoints.put("/api/v1/auth/password-recovery-request", Collections.singletonList(HttpMethod.POST));
        openApiEndpoints.put("/api/v1/auth/password-recovery", Collections.singletonList(HttpMethod.POST));
        openApiEndpoints.put("/api/v1/user-search/simple-lookup", Collections.singletonList(HttpMethod.GET));
        openApiEndpoints.put("/api/v1/post/user/{userId}", Collections.singletonList(HttpMethod.GET));
        openApiEndpoints.put("/api/v1/post/{postId}", Collections.singletonList(HttpMethod.GET));
        openApiEndpoints.put("/api/v1/post-search/simple-hashtag-lookup", Collections.singletonList(HttpMethod.GET));
        openApiEndpoints.put("/eureka", Collections.singletonList(HttpMethod.GET));
    }

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public Predicate<ServerHttpRequest> isSecured = request ->
            openApiEndpoints.keySet().stream().noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()) && openApiEndpoints.get(uri).contains(request.getMethod()));
}
