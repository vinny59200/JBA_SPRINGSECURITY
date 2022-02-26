package account.management.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Role Operation not found!")
public class RoleOperationNotFoundException extends RuntimeException {
    public RoleOperationNotFoundException(String role_not_found) {
        super(role_not_found);
    }
}

