package account.management;


import account.management.entities.EventAction;
import account.management.exceptions.InvalidEventLogException;
import account.management.service.EventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class EventLogger {

    EventService service;

    public void log(final Long offset, final EventAction action, final String... args) {

        if (args.length == 2) {

            service.save(offset, action, args[0], args[1], args[1]);

        } else if (args.length == 3) {

            service.save(offset, action, args[0], args[1], args[2]);

        } else {

            throw new InvalidEventLogException("Expected 2 or 3 String arguments, got " + args.length + "!");

        }

    }

}
