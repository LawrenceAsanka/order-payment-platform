package lk.asanka.orders.client;

import java.util.UUID;

public interface ProductClient {

    ProductResponse getProductPrice(UUID productId);
}
