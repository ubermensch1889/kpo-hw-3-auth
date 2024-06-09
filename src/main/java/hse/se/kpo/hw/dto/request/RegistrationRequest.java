package hse.se.kpo.hw.dto.request;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String nickname;
    private String email;
    private String password;
}
