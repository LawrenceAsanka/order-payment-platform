package lk.asanka.orders.client;

import lk.asanka.orders.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public ProductResponse getProductPrice(UUID productId) {
        try {
            return productServiceRestClient
                    .get()
                    .uri("/api/v1/products/{productId}", productId)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
    }
}