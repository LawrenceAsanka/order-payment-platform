package lk.asanka.orders.client;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID productId, BigDecimal price) {
}
