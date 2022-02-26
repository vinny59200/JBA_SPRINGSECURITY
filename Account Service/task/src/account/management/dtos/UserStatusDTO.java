package account.management.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class UserStatusDTO {
    @JsonProperty("user")
    private String user;

    @JsonProperty("status")
    private String status;
}
