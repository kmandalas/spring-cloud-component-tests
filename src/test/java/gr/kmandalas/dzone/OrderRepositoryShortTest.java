package gr.kmandalas.dzone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest(properties = {"spring.test.database.replace=NONE",
                           "spring.datasource.url=jdbc:tc:postgresql:12:///tutorial"})
class OrderRepositoryShortTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Sql("/scripts/init_test_data.sql")
    void shouldReturnOrdersThatContainMacBookPro() {
        List<Order> orders = orderRepository.findAllContainingItemSpecial();
        assertEquals(2, orders.size());
    }

}
