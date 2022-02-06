package gr.kmandalas.dzone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AuthenticationResponse {

    private final String jwtToken;
}
