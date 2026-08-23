package lk.asanka.orders.service.impl;

import lk.asanka.orders.client.ProductClient;
import lk.asanka.orders.client.ProductResponse;
import lk.asanka.orders.dto.CreateOrderItemRequest;
import lk.asanka.orders.dto.CreateOrderRequest;
import lk.asanka.orders.dto.OrderResponse;
import lk.asanka.orders.entity.Order;
import lk.asanka.orders.entity.OrderItem;
import lk.asanka.orders.enums.OrderStatus;
import lk.asanka.orders.respository.OrderRepository;
import lk.asanka.orders.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(ProductClient productClient, OrderRepository orderRepository) {
        this.productClient = productClient;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        BigDecimal orderTotal = BigDecimal.ZERO;
        Order order = new Order();

        List<CreateOrderItemRequest> items = request.items();
        for (CreateOrderItemRequest item : items) {
            ProductResponse productPrice = productClient.getProductPrice(item.productId());
            orderTotal = orderTotal.add(productPrice.price().multiply(BigDecimal.valueOf(item.quantity())));

            OrderItem orderItem = new OrderItem(productPrice.productId(), item.quantity(), productPrice.price());
            order.addItem(orderItem);
        }
        order.setTotalAmount(orderTotal);
        order.setStatus(OrderStatus.NEW);
        order.setCustomerId(request.customerId());

        Order saveOrder = orderRepository.save(order);
        return new OrderResponse(saveOrder.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt());
    }
}
