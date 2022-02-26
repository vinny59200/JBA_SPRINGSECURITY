package account.management.dtos;


import account.management.entities.EventAction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class EventDto {

    Long          id;
    LocalDateTime date;
    EventAction action;
    String        subject;
    String        object;
    String        path;

}