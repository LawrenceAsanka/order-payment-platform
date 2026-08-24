package lk.asanka.orders.integration;

import lk.asanka.orders.client.ProductClient;
import lk.asanka.orders.client.ProductResponse;
import lk.asanka.orders.dto.CreateOrderItemRequest;
import lk.asanka.orders.dto.CreateOrderRequest;
import lk.asanka.orders.entity.Order;
import lk.asanka.orders.enums.OrderStatus;
import lk.asanka.orders.exception.ProductNotFoundException;
import lk.asanka.orders.respository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductClient productClient;

    @Test
    void createOrder_shouldReturnOkAndPersistOrder_whenRequestIsValid() throws Exception {

        UUID productId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(
                        new CreateOrderItemRequest(
                                productId,
                                2
                        )
                )
        );

        when(productClient.getProductPrice(productId))
                .thenReturn(new ProductResponse(
                        productId,
                        new BigDecimal("25.00")
                ));

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        Order order = orderRepository.findAll()
                .stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .findFirst()
                .orElseThrow();

        assertEquals(customerId, order.getCustomerId());
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(new BigDecimal("50.00"), order.getTotalAmount());
    }

    @Test
    void createOrder_shouldReturnNotFoundAndNotPersistOrder_whenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(
                        new CreateOrderItemRequest(
                                productId,
                                2
                        )
                )
        );

        when(productClient.getProductPrice(productId))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        assertTrue(
                orderRepository.findAll()
                        .stream()
                        .noneMatch(order ->
                                order.getCustomerId().equals(customerId))
        );

    }
}
