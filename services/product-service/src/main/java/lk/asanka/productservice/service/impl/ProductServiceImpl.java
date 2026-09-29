package lk.asanka.productservice.service.impl;

import lk.asanka.productservice.entity.Product;
import lk.asanka.productservice.client.ProductResponse;
import lk.asanka.productservice.exception.ProductNotFoundException;
import lk.asanka.productservice.repository.ProductRepository;
import lk.asanka.productservice.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse getProductById(UUID productId) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product not found :" + productId));

        return new ProductResponse(product.getProductId(), product.getName(), product.getPrice());
    }
}
