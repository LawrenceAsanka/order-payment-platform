package lk.asanka.orders.repository;

import lk.asanka.orders.entity.Order;
import lk.asanka.orders.entity.OrderItem;
import lk.asanka.orders.enums.OrderStatus;
import lk.asanka.orders.respository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;


@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save_shouldPersistOrder_whenOrderIsValid(){
        Order order = new Order();

        OrderItem orderItem = new OrderItem(UUID.randomUUID(), 2, new BigDecimal("25.00"));
        order.addItem(orderItem);
        order.setCustomerId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(new BigDecimal("50.00"));

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals(OrderStatus.NEW, savedOrder.getStatus());
        assertEquals(new BigDecimal("50.00"), savedOrder.getTotalAmount());
    }

    @Test
    void findById_shouldReturnOrder_whenOrderExists() {
        Order order = new Order();

        OrderItem orderItem = new OrderItem(UUID.randomUUID(), 2, new BigDecimal("25.00"));
        order.addItem(orderItem);
        order.setCustomerId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(new BigDecimal("50.00"));

        Order savedOrder = orderRepository.save(order);
        assertNotNull(savedOrder.getId());

        Optional<Order> optionalOrder = orderRepository.findById(savedOrder.getId());
        assertTrue(optionalOrder.isPresent());

        Order foundOrder = optionalOrder.get();

        assertEquals(savedOrder.getId(), foundOrder.getId());
        assertEquals(OrderStatus.NEW, foundOrder.getStatus());
        assertEquals(new BigDecimal("50.00"), foundOrder.getTotalAmount());
    }

    @Test
    void save_shouldPersistOrderItems_whenOrderHasItems() {
        Order order = new Order();

        OrderItem orderItem = new OrderItem(UUID.randomUUID(), 2, new BigDecimal("25.00"));
        order.addItem(orderItem);
        order.setCustomerId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setTotalAmount(new BigDecimal("50.00"));

        Order savedOrder = orderRepository.save(order);
        assertNotNull(savedOrder.getId());

        Order foundOrder = orderRepository.findById(savedOrder.getId())
                .orElseThrow();

        assertEquals(1, foundOrder.getItems().size());

        OrderItem savedItem = foundOrder.getItems().get(0);
        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("25.00"), savedItem.getUnitPrice());
    }
}
