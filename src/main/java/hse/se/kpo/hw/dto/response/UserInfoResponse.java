package hse.se.kpo.hw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private String nickname;
    private String email;
}
