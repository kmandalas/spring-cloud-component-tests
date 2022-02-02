package gr.kmandalas.dzone;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "fulfillment-service")
public interface FulfillmentClient {

  @GetMapping(value = "/v1/status/{trackingNumber}", produces = APPLICATION_JSON_VALUE)
  FulfillmentStatus getDeliveryStatus(@PathVariable String trackingNumber);

}
