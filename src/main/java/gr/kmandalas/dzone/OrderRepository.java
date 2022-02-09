package gr.kmandalas.dzone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM orders WHERE items @> '[{\"name\": \"Special\"}]';", nativeQuery = true)
    List<Order> findAllContainingItemSpecial();

    Optional<Order> findDistinctByTrackingNumber(String trackingNumber);

}
