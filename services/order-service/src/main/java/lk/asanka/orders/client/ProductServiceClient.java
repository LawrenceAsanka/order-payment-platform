package lk.asanka.orders.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
        return null;
    }
}