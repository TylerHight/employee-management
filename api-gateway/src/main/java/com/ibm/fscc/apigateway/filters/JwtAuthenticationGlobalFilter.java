package com.ibm.fscc.apigateway.filters;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ibm.fscc.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationGlobalFilter.class);
    private final JwtUtil jwtUtil;

    // Public methods+paths
    private static final List<PublicRoute> PUBLIC_ROUTES = List.of(
            new PublicRoute(HttpMethod.POST, "/api/login/authenticate"),
            new PublicRoute(HttpMethod.POST, "/api/login/set-password"),
            new PublicRoute(HttpMethod.POST, "/api/login/request-password-reset"),
            new PublicRoute(HttpMethod.POST, "/api/login/reset-password"),
            new PublicRoute(HttpMethod.GET, "/api/login/status/check"),
            new PublicRoute(HttpMethod.POST, "/api/registration"),
            new PublicRoute(HttpMethod.GET, "/api/registration/status/check"),
            new PublicRoute(HttpMethod.GET, "/api/employees/status/check"),
            // OpenAPI documentation endpoints (all services)
            new PublicRoute(HttpMethod.GET, "/employee-service/v3/api-docs"),
            new PublicRoute(HttpMethod.GET, "/employee-service/swagger-ui.html"),
            new PublicRoute(HttpMethod.GET, "/registration-service/v3/api-docs"),
            new PublicRoute(HttpMethod.GET, "/registration-service/swagger-ui.html"),
            new PublicRoute(HttpMethod.GET, "/login-service/v3/api-docs"),
            new PublicRoute(HttpMethod.GET, "/login-service/swagger-ui.html"),
            new PublicRoute(HttpMethod.GET, "/notification-service/v3/api-docs"),
            new PublicRoute(HttpMethod.GET, "/notification-service/swagger-ui.html"));

    // Protected paths
    private static final List<String> PROTECTED_PATH_PREFIXES = List.of(
            "/api/employees",
            "/api/registration/");

    public JwtAuthenticationGlobalFilter(JwtUtil jwtUtil) {
        logger.info("Initializing JWT Authentication Global Filter");
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Skip authentication for public routes
        if (isPublicRoute(method, path)) {
            logger.debug("Skipping authentication for public route: {} {}", method, path);
            return chain.filter(exchange);
        }

        // Require authentication for protected routes
        if (requiresAuthentication(path)) {
            logger.debug("Applying JWT authentication for path: {}", path);

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            var claims = jwtUtil.extractClaims(token);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.get("userId", String.class))
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        // Pass through if no rule matched
        return chain.filter(exchange);
    }

    private boolean isPublicRoute(HttpMethod method, String path) {
        // Normalize: remove trailing slash (except root "/") and lowercase
        String normalizedPath = normalizePath(path);

        // Allow all Swagger UI resources (CSS, JS, images, etc.) and OpenAPI docs
        if (normalizedPath.contains("/swagger-ui") ||
            normalizedPath.contains("/v3/api-docs") ||
            normalizedPath.contains("/swagger-resources") ||
            normalizedPath.contains("/webjars/")) {
            return true;
        }

        return PUBLIC_ROUTES.stream()
                .anyMatch(route -> route.method.equals(method)
                        && normalizePath(route.path).equals(normalizedPath));
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        // Remove trailing slash if not root
        String noSlash = (path.endsWith("/") && path.length() > 1)
                ? path.substring(0, path.length() - 1)
                : path;
        return noSlash.toLowerCase();
    }

    private boolean requiresAuthentication(String path) {
        String normalizedPath = normalizePath(path);
        return PROTECTED_PATH_PREFIXES.stream()
                .map(this::normalizePath)
                .anyMatch(normalizedPath::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        logger.warn("{} - Path: {}", message, exchange.getRequest().getPath());
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0; // After logging filter
    }

    // Helper class for method+path pairing
    private static class PublicRoute {
        final HttpMethod method;
        final String path;

        PublicRoute(HttpMethod method, String path) {
            this.method = method;
            this.path = path;
        }
    }
}
