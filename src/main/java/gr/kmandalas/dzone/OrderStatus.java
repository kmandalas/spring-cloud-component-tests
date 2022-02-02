package gr.kmandalas.dzone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatus {

    private String trackingNumber;
    private String status;
    private String smsSent;
    private String location;

}
