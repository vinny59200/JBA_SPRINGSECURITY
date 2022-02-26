package account.management.service;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AuthAttemptService {

    Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    public void loginSuccess(final String key) {
        log.error("VV83 - loginSuccess");
        attemptsCache.remove(key);

    }

    public void loginFailure(final String key) {
        log.error("VV83 - loginFailure");
        if (attemptsCache.containsKey(key)) {

            attemptsCache.put(key, attemptsCache.get(key) + 1);

        } else {

            attemptsCache.put(key, 1);

        }

    }

    public boolean isBruteForce(final String key) {
        log.error("VV83 - isBruteForce");
        if (attemptsCache.containsKey(key)) {

            return attemptsCache.get(key) == 5;

        }

        return false;

    }

}
