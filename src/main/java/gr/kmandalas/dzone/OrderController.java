package gr.kmandalas.dzone;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

 private final OrderService orderService;

  @GetMapping
  public List<Order> getAllOrders() {
    return orderService.getAllOrders();
  }

  @GetMapping("/{trackingNumber}/status")
  public OrderStatus getOrderStatus(@PathVariable String trackingNumber) {
    return orderService.getOrderStatus(trackingNumber);
  }

}
