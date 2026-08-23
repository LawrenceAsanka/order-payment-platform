package lk.asanka.orders.controller;

import lk.asanka.orders.dto.CreateOrderItemRequest;
import lk.asanka.orders.dto.CreateOrderRequest;
import lk.asanka.orders.dto.OrderResponse;
import lk.asanka.orders.enums.OrderStatus;
import lk.asanka.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void health_shouldReturnUp() throws Exception {

        mockMvc.perform(
                        get("/api/v1/orders/health")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void createOrder_shouldReturnOrderResponse_whenValidRequest() throws Exception {
        OrderResponse orderResponse = new OrderResponse(
                UUID.randomUUID(),
                OrderStatus.NEW,
                new BigDecimal("25.00"),
                Instant.now()
        );

        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemRequest(
                                UUID.randomUUID(),
                                2
                        )
                )
        );

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(orderResponse);

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalAmount").value(25.0))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createOrder_shouldReturnBadRequest_whenQuantityIsNotPositive() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemRequest(
                                UUID.randomUUID(),
                                0
                        )
                )
        );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_shouldReturnBadRequest_whenProductIdIsNull() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemRequest(
                                null,
                                2
                        )
                )
        );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_shouldReturnBadRequest_whenCustomerIdIsNull() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                null,
                List.of(
                        new CreateOrderItemRequest(
                                UUID.randomUUID(),
                                2
                        )
                )
        );

        mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
