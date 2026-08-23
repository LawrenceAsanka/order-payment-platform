package lk.asanka.orders.service;

import lk.asanka.orders.client.ProductClient;
import lk.asanka.orders.client.ProductResponse;
import lk.asanka.orders.dto.CreateOrderItemRequest;
import lk.asanka.orders.dto.CreateOrderRequest;
import lk.asanka.orders.dto.OrderResponse;
import lk.asanka.orders.entity.Order;
import lk.asanka.orders.exception.ProductNotFoundException;
import lk.asanka.orders.respository.OrderRepository;
import lk.asanka.orders.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_shouldCalculateTotalAndSaveOrder_whenRequestIsValid() {
        UUID productId = UUID.randomUUID();
        ProductResponse productResponse = new ProductResponse(productId, new BigDecimal("25.00"));
        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemRequest(
                                productId,
                                2
                        )
                )
        );

        when(productClient.getProductPrice(productId))
                .thenReturn(productResponse);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse orderResponse = orderService.createOrder(request);

        assertEquals(new BigDecimal("50.00"), orderResponse.totalAmount());
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertEquals(
                new BigDecimal("50.00"),
                savedOrder.getTotalAmount()
        );
    }

    @Test
    void createOrder_shouldThrowExceptionAndNotSaveOrder_whenProductNotFound() {

        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemRequest(
                                productId,
                                2
                        )
                )
        );
        when(productClient.getProductPrice(productId))
                .thenThrow(ProductNotFoundException.class);

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }
}
