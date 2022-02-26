package account.management.repository;


import account.management.entities.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAllByOrderByIdAsc();

    Optional<Event> findFirstByOrderByIdDesc();

}
