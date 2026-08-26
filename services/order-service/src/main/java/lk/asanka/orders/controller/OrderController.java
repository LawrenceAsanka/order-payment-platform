package lk.asanka.orders.controller;

import jakarta.validation.Valid;
import lk.asanka.orders.dto.CreateOrderRequest;
import lk.asanka.orders.dto.HealthResponse;
import lk.asanka.orders.dto.OrderResponse;
import lk.asanka.orders.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/health")
    public HealthResponse checkHealth(){
        return new HealthResponse("UP");
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest){
        return orderService.createOrder(createOrderRequest);
    }
}
