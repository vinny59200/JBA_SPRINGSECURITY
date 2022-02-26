package account.management.service.impl;


import account.management.dtos.EventDto;
import account.management.entities.Event;
import account.management.entities.EventAction;
import account.management.repository.EventRepository;
import account.management.service.EventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;

    @Override
    public List<EventDto> getEvents() {
        List<Event> events = eventRepository.findAllByOrderByIdAsc();
        if (events == null) {
            return null;
        }

        List<EventDto> list = new ArrayList<EventDto>(events.size());
        for (Event event : events) {
            if (event == null) {
                return null;
            }

            EventDto.EventDtoBuilder eventDto = EventDto.builder();

            eventDto.id(event.getId());
            eventDto.date(event.getDate());
            eventDto.action(event.getAction());
            eventDto.subject(event.getSubject());
            eventDto.object(event.getObject());
            eventDto.path(event.getPath());

            list.add(eventDto.build());
        }
        log.error("VV6 list event: " + list);
        return list;
    }

    @Override
    public void save(final Long offset, final EventAction action, final String subject, final String object, final String path) {
        eventRepository.save(new Event(getNextId() + (offset > 0 ? offset - 1 : 0), LocalDateTime.now(), action, subject, object, path));
    }

    private Long getNextId() {

        Optional<Event> optionalEvent = eventRepository.findFirstByOrderByIdDesc();

        return optionalEvent.isEmpty() ? 1 : (optionalEvent.get().getId() + 1);

    }
}
