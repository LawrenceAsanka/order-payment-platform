package lk.asanka.orders.service;

import lk.asanka.orders.dto.CreateOrderRequest;
import lk.asanka.orders.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);
}
