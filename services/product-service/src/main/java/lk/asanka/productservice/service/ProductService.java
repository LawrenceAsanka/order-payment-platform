package lk.asanka.productservice.service;

import lk.asanka.productservice.client.ProductResponse;

import java.util.UUID;

public interface ProductService {
    ProductResponse getProductById(UUID productId);
}
