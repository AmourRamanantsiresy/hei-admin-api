package school.hei.haapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.haapi.model.Event;
public interface EventRepository extends JpaRepository<Event, String> {
}
