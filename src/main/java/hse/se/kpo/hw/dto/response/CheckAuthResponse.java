package hse.se.kpo.hw.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckAuthResponse {
    private int userId;
    private Boolean isAuthorized;
}
