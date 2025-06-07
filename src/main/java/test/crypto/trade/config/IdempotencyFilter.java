package test.crypto.trade.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import test.crypto.trade.response.CachedResponse;
import test.crypto.trade.service.IdempotencyService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<String> idempotencyUrls;

    public IdempotencyFilter(@Value("${app.Idempotency.checkUrls}") String urls) {
        this.idempotencyUrls = Arrays.stream(urls.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String path = request.getRequestURI();

        boolean needCheckIdempotency = "POST".equalsIgnoreCase(method) && isIdempotentPath(path);

        if (needCheckIdempotency) {
            String idempotencyKey = request.getHeader("Idempotency-Key");
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing Idempotency-Key header");
                return;
            }

            Optional<String> existing = idempotencyService.getSavedResponse(idempotencyKey);
            if (existing.isPresent()) {
                CachedResponse cached = objectMapper.readValue(existing.get(), CachedResponse.class);
                response.setStatus(cached.getStatus());
                response.setContentType(cached.getContentType());
                response.getWriter().write(cached.getBody());
                return;
            }

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

            filterChain.doFilter(wrappedRequest, wrappedResponse);

            String responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());
            CachedResponse cachedResponse = new CachedResponse(
                    wrappedResponse.getStatus(), wrappedResponse.getContentType(), responseBody);

            idempotencyService.saveResponse(idempotencyKey, objectMapper.writeValueAsString(cachedResponse), path);

            wrappedResponse.copyBodyToResponse();

        } else {
            filterChain.doFilter(request, response);
        }
    }


    private boolean isIdempotentPath(String path) {
        return idempotencyUrls.stream().anyMatch(path::equals);
    }
}
