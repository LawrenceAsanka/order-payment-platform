package lk.asanka.orders.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lk.asanka.orders.exception.ProductNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "resilience4j.circuitbreaker.instances.productService.slidingWindowSize=4",
        "resilience4j.circuitbreaker.instances.productService.minimumNumberOfCalls=4",
        "resilience4j.circuitbreaker.instances.productService.failureRateThreshold=50",
        "resilience4j.circuitbreaker.instances.productService.waitDurationInOpenState=1s",
        "resilience4j.circuitbreaker.instances.productService.recordExceptions=org.springframework.web.client.HttpServerErrorException",
        "resilience4j.retry.instances.productService.retryExceptions=org.springframework.web.client.HttpServerErrorException"
})
public class ProductServiceClientTest {

    private static WireMockServer wireMockServer;
    @Autowired
    private ProductServiceClient productServiceClient;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "product-service.base-url",
                () -> "http://localhost:" + wireMockServer.port()
        );
    }

    @Test
    void getProductPrice_shouldRetry_whenProductServiceReturns500() {

        UUID productId = UUID.randomUUID();

        wireMockServer.stubFor(
                get(urlPathEqualTo("/api/v1/products/" + productId))
                        .willReturn(aResponse().withStatus(500))
        );

        assertThatThrownBy(() ->
                productServiceClient.getProductPrice(productId)
        );

        wireMockServer.verify(
                3,
                getRequestedFor(urlPathEqualTo("/api/v1/products/" + productId))
        );
    }

    @Test
    void getProductPrice_shouldNotRetry_whenProductNotFound() {
        UUID productId = UUID.randomUUID();

        wireMockServer.stubFor(
                get(urlPathEqualTo("/api/v1/products/" + productId))
                        .willReturn(aResponse().withStatus(404))
        );

        assertThatThrownBy(() ->
                productServiceClient.getProductPrice(productId)
        ).isInstanceOf(ProductNotFoundException.class);

        wireMockServer.verify(
                1,
                getRequestedFor(urlPathEqualTo("/api/v1/products/" + productId))
        );
    }

    @Test
    void getProductPrice_shouldOpenCircuitBreaker_whenProductServiceKeepsFailing() {
        UUID productId = UUID.randomUUID();

        wireMockServer.stubFor(
                get(urlPathEqualTo("/api/v1/products/" + productId))
                        .willReturn(aResponse().withStatus(500))
        );

        for (int i = 0; i < 4; i++) {
            assertThatThrownBy(() ->
                    productServiceClient.getProductPrice(productId)
            );
        }

        assertThatThrownBy(() ->
                productServiceClient.getProductPrice(productId)
        ).isInstanceOf(CallNotPermittedException.class);

        wireMockServer.verify(
                4,
                getRequestedFor(urlPathEqualTo("/api/v1/products/" + productId))
        );
    }
}
