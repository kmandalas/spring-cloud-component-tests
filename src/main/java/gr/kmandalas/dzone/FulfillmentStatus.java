package gr.kmandalas.dzone;

import lombok.Value;

@Value
public class FulfillmentStatus {

    String trackingNumber;
    String status;
    String smsSent;

}
