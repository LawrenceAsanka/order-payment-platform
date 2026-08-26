package lk.asanka.orders.dto;

import lk.asanka.orders.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID orderId, OrderStatus status, BigDecimal totalAmount, Instant createdAt) {
}
