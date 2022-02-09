package gr.kmandalas.dzone;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Testcontainers
@AutoConfigureWireMock(port=9999)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Container
    static PostgreSQLContainer database = new PostgreSQLContainer("postgres:12")
        .withDatabaseName("tutorial")
        .withUsername("kmandalas")
        .withPassword("dzone2022");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
        propertyRegistry.add("spring.datasource.password", database::getPassword);
        propertyRegistry.add("spring.datasource.username", database::getUsername);
    }

    @TestConfiguration
    protected static class TestConfig {

        @Bean
        public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier() {
            return new ServiceInstanceListSupplier() {
                @Override
                public String getServiceId() {
                    return "wiremock";
                }

                @Override
                public Flux<List<ServiceInstance>> get() {
                    ServiceInstance instance1 = new ServiceInstance() {
                        @Override
                        public String getServiceId() {
                            return "wiremock";
                        }

                        @Override
                        public String getHost() {
                            return "localhost";
                        }

                        @Override
                        public int getPort() {
                            return 9999;
                        }

                        @Override
                        public boolean isSecure() {
                            return false;
                        }

                        @Override
                        public URI getUri() {
                            return URI.create("http://localhost:9999");
                        }

                        @Override
                        public Map<String, String> getMetadata() {
                            return null;
                        }
                    };

                    Flux<ServiceInstance> serviceInstances = Flux.defer(() ->
                        Flux.fromIterable(List.of(instance1))).subscribeOn(Schedulers.boundedElastic());
                    return serviceInstances.collectList().flux();
                }
            };
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void getStatus_withValidJwtToken_returnsOk() throws Exception {
        orderRepository.save(createOrder("11212",
            "   [{\"name\": \"Item 1\", \"amount\" : 300}, {\"name\": \"Item2\", \"amount\" : 180}]\n"));
        mockMvc.perform(get("/api/orders/11212/status").with(jwt())).andExpect(status().isOk());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void getStatus_withInvalidTrackingNumber_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/11212/status").with(jwt())).andExpect(status().isNotFound());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void getAllOrders_withValidJwtToken_returnsOk() throws Exception {
        mockMvc.perform(get("/api/orders").with(jwt().authorities(new SimpleGrantedAuthority("backoffice"))))
               .andExpect(status().isOk());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void getAllOrders_withMissingAuthorities_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/orders").with(jwt())).andExpect(status().isForbidden());
    }

    private Order createOrder(String trackingNumber, String items) {
        Order order = new Order();
        order.setTrackingNumber(trackingNumber);
        order.setItems(items);
        return order;
    }

}
