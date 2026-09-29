package lk.asanka.orders.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lk.asanka.orders.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ProductServiceClient implements ProductClient {

    private final RestClient productServiceRestClient;

    public ProductServiceClient(RestClient.Builder restClientBuilder,
                                @Value("${product-service.base-url}") String productServiceBaseUrl) {
        productServiceRestClient = restClientBuilder.baseUrl(productServiceBaseUrl)
                .build();
    }

    @CircuitBreaker(name = "productService")
    @Retry(name = "productService")
    @Override
    public ProductResponse getProductPrice(UUID productId) {
        String token = null;
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken){
            token = jwtAuthenticationToken.getToken().getTokenValue();
        }

        try {
            return productServiceRestClient
                    .get()
                    .uri("/api/v1/products/{productId}", productId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
    }
}