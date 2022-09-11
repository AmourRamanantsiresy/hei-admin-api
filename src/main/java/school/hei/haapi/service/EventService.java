package school.hei.haapi.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.haapi.model.Event;
import school.hei.haapi.model.User;
import school.hei.haapi.repository.EventRepository;
import school.hei.haapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Event getById(String id) {
        return eventRepository.getById(id);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public List<Event> saveAll(List<Event> events){
        return eventRepository.saveAll(
                events.stream()
                        .map(e-> {
                            User responsible = userRepository.getById(e.getResponsible().getId());
                            Event event = e;
                            event.setResponsible(responsible);
                            return event;
                        })
                        .collect(Collectors.toUnmodifiableList())
        );
    }
}
