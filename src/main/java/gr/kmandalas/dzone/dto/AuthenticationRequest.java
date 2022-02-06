package gr.kmandalas.dzone.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthenticationRequest {

    private String username;
    private String password;
}
