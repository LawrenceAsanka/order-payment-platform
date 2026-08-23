package lk.asanka.orders.client;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ProductServiceClient implements ProductClient{

    private final RestClient restClient;


    public ProductServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ProductResponse getProductPrice(UUID productId) {
        return null;
    }
}
