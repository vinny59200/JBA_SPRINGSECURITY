package account.management.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Role not found!")
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String role_not_found) {
        super(role_not_found);
    }
}

