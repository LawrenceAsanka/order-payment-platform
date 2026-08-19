package lk.asanka.orders.controller;

import lk.asanka.orders.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping("/health")
    public HealthResponse checkHealth(){
        return new HealthResponse("UP");
    }
}
