package account.management.service;


import account.management.dtos.EventDto;
import account.management.entities.EventAction;

import java.util.List;

public interface EventService {

    List<EventDto> getEvents();

    void save(final Long offset, final EventAction action, final String subject, final String object, final String path);

}