//package school.hei.haapi.service;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import school.hei.haapi.model.Event;
//import school.hei.haapi.model.User;
//import school.hei.haapi.repository.EventRepository;
//import school.hei.haapi.repository.UserRepository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@AllArgsConstructor
//public class EventParticipantService {
//    public EventRepository eventRepository;
//
//    public UserRepository userRepository;
//
//    public List<User> getExpectedParticipant(Event event){
//        return eventRepository.getById(event.getId()).getExpectedUser();
//    }
//
//    public List<User> getPresentParticipants(Event event){
//        return eventRepository.getById(event.getId()).getExpectedUser();
//    }
//
//    public List<User> getAbsentParticipants(Event event){
//        Event currentEvent = eventRepository.getById(event.getId());
//        return currentEvent.getExpectedUser().stream()
//                .filter(e-> !currentEvent.getPresentUser().contains(e))
//                .collect(Collectors.toUnmodifiableList());
//    }
//
//    public List<User> makeParticipantPresent(Event event, User participant){
//        Event currentEvent = eventRepository.getById(event.getId());
//        User toPresentUser = userRepository.getById(participant.getId());
//
//        List<User> presentParticipant = currentEvent.getPresentUser();
//        presentParticipant.add(toPresentUser);
//
//        currentEvent.setPresentUser(presentParticipant);
//        return eventRepository.save(currentEvent).getPresentUser();
//    }
//
//    public List<User> save(Event event, List<User> expectedParticipants){
//        Event currentEvent = eventRepository.getById(event.getId());
//        List<User> presentParticipant = currentEvent.getPresentUser();
//        expectedParticipants.forEach(e-> {
//            User toPresentParticipant = userRepository.getById(e.getId());
//            presentParticipant.add(toPresentParticipant);
//        });
//        currentEvent.setPresentUser(presentParticipant);
//        return eventRepository.save(currentEvent).getPresentUser();
//    }
//}
