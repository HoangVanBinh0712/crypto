package test.crypto.trade.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.modelmapper.ModelMapper;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Value("${resttemplate.connection-timeout}")
    private int timeout;

    @Value("${resttemplate.connection-pool.total}")
    private int poolSize;

    @Value("${resttemplate.connection-pool.connection-per-route}")
    private int connectionPerRoute;
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate() {

        // Connection pool
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(poolSize); // Max total connections
        poolingConnManager.setDefaultMaxPerRoute(connectionPerRoute); // Max connections per route

        // Timeout settings
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout, TimeUnit.MICROSECONDS)
                .build();

        // Create HttpClient
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolingConnManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        // Set HttpClient to RestTemplate
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);

    }

    @Bean
    public Cache<String, Object> priceSnapshotCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Bean
    public OpenApiCustomizer customHeaderSwagger(@Value("${app.Idempotency.checkUrls}") String checkUrls,
                                                 @Value("${server.servlet.context-path:}") String contextPath) {
        List<String> idempotencyPaths = Arrays.stream(checkUrls.split(","))
                .map(String::trim)
                .map(path -> path.startsWith(contextPath) ? path.substring(contextPath.length()) : path)
                .toList();


        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((path, pathItem) -> {
                    // Check if this path is in the idempotency list
                    if (idempotencyPaths.stream().anyMatch(path::equals)) {
                        pathItem.readOperations().forEach(operation -> {
                            boolean alreadyExists = operation.getParameters() != null &&
                                    operation.getParameters().stream()
                                            .anyMatch(p -> "Idempotency-Key".equalsIgnoreCase(p.getName()));
                            if (!alreadyExists) {
                                operation.addParametersItem(new Parameter()
                                        .in(ParameterIn.HEADER.toString())
                                        .name("Idempotency-Key")
                                        .description("Idempotency Key to prevent duplicate requests")
                                        .required(false)
                                        .schema(new StringSchema()));
                            }
                        });
                    }
                });
            }
        };
    }
}
