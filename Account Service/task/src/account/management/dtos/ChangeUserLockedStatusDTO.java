package account.management.dtos;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Locale;

@Getter
@Setter
public class ChangeUserLockedStatusDTO {
    @Email
    @NotBlank
    private String user;

    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;

    public String getUser() {
        return user;
    }

    public String getOperationCapitalized(){
        return this.operation.substring(0, 1).toUpperCase() + this.operation.substring(1).toLowerCase();
    }

}
