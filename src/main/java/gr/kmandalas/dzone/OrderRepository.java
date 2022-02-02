package gr.kmandalas.dzone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM orders WHERE items @> '[{\"name\": \"Special\"}]';", nativeQuery = true)
    List<Order> findAllContainingItemSpecial();

    Order findDistinctByTrackingNumber(String trackingNumber);

}
