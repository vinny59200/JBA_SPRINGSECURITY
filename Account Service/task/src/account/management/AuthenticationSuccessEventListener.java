package account.management;

import account.management.service.AuthAttemptService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    AuthAttemptService service;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent event) {

        service.loginSuccess(event.getAuthentication().getName().toLowerCase());

    }
}
