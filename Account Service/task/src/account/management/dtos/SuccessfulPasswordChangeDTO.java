package account.management.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class SuccessfulPasswordChangeDTO {

    @JsonProperty("email")
    private String email;
    @JsonProperty("status")
    private String status;

}
