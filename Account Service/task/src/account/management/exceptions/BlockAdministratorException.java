package account.management.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.BAD_REQUEST,reason = "Can't lock the ADMINISTRATOR!")
public class BlockAdministratorException extends RuntimeException {
}
