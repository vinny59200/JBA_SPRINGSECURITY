package account.management;

import account.management.exceptions.UserLockedException;
import account.management.service.AuthAttemptService;
import lombok.AccessLevel;
import account.management.entities.EventAction;
import account.management.service.UserService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    HttpServletRequest request;
    @Autowired
    AuthAttemptService service;
    @Autowired
    UserService userService;
    @Autowired
    EventLogger logger;

    @Override
    public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent event) {

        String subject = event.getAuthentication().getName().toLowerCase();

        service.loginFailure(subject);
        logger.log(0L, EventAction.LOGIN_FAILED, subject, request.getServletPath());

        if (service.isBruteForce(subject)) {

            logger.log(0L, EventAction.BRUTE_FORCE, subject, request.getServletPath());
            userService.updateUserIsAccountNonLocked(subject, false,"LOCK",true);
            log.error("VV55 User {} is locked due to brute force attack", subject);
        }

    }

}
