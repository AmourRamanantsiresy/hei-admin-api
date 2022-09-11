package school.hei.haapi.endpoint.rest.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.haapi.model.Event;

@Component
@AllArgsConstructor
public class EventMapper {
    private UserMapper userMapper;

    public school.hei.haapi.endpoint.rest.model.Event toRest(Event event) {
        var restEvent = new school.hei.haapi.endpoint.rest.model.Event();
        restEvent.setId(event.getId());
        restEvent.setRef(event.getRef());
        restEvent.setName(event.getName());
        restEvent.setStartingDate(event.getStartingDate());
        restEvent.setEndingDate(event.getEndingDate());
        restEvent.setResponsible(userMapper.toRestParticipant(event.getResponsible()));
        return restEvent;
    }

    public Event toDomain(school.hei.haapi.endpoint.rest.model.Event restEvent) {
        return Event.builder()
                .id(restEvent.getId())
                .ref(restEvent.getRef())
                .name(restEvent.getName())
                .startingDate(restEvent.getStartingDate())
                .endingDate(restEvent.getEndingDate())
                .responsible(userMapper.toDomain(restEvent.getResponsible())).build();
    }
}
