package gr.kmandalas.dzone;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final FulfillmentClient fulfillmentClient;
    private final RestTemplate restTemplate;

    @Value("${location-service.url}")
    private String locationServiceUrl;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderStatus getOrderStatus(String trackingNumber) {
        // 1. Data retrieval from a relational database
        var order = orderRepository.findDistinctByTrackingNumber(trackingNumber); // todo: add NOT_FOUND case and test
        OrderStatus os = new OrderStatus();
        os.setTrackingNumber(order.getTrackingNumber());

        // 2. internal feign call to get the delivery status
        var fulfillmentStatus = fulfillmentClient.getDeliveryStatus(trackingNumber);
        os.setStatus(fulfillmentStatus.getStatus());
        os.setSmsSent(fulfillmentStatus.getSmsSent());

        // 3. external call to get the item's location
        if ("IN_TRANSIT".equals(os.getStatus())) { // todo: add a trivial enum
            var location = restTemplate
                .getForEntity(locationServiceUrl + os.getTrackingNumber(), String.class).getBody();
            os.setLocation(location);
        }

        return os;
    }

}
