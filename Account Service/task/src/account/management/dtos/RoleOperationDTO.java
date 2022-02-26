package account.management.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class RoleOperationDTO {

    @JsonProperty("user")
    private String user;

    @JsonProperty("role")
    private String role;

    @JsonProperty("operation")
    private RoleOperation operation;
}
