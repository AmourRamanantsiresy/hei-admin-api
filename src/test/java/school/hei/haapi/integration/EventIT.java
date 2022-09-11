package school.hei.haapi.integration;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.hei.haapi.SentryConf;
import school.hei.haapi.endpoint.rest.api.EventsApi;
import school.hei.haapi.endpoint.rest.client.ApiClient;
import school.hei.haapi.endpoint.rest.client.ApiException;
import school.hei.haapi.endpoint.rest.mapper.UserMapper;
import school.hei.haapi.endpoint.rest.model.Event;
import school.hei.haapi.endpoint.rest.model.Participant;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static school.hei.haapi.integration.conf.TestUtils.BAD_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.EVENT1_ID;
import static school.hei.haapi.integration.conf.TestUtils.EVENT2_ID;
import static school.hei.haapi.integration.conf.TestUtils.MANAGER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.STUDENT1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.TEACHER1_ID;
import static school.hei.haapi.integration.conf.TestUtils.TEACHER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.TEACHER2_ID;
import static school.hei.haapi.integration.conf.TestUtils.anAvailableRandomPort;
import static school.hei.haapi.integration.conf.TestUtils.assertThrowsForbiddenException;
import static school.hei.haapi.integration.conf.TestUtils.isValidUUID;
import static school.hei.haapi.integration.conf.TestUtils.setUpCognito;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = EventIT.ContextInitializer.class)
@AutoConfigureMockMvc
public class EventIT {

    @MockBean
    private SentryConf sentryConf;

    @MockBean
    private CognitoComponent cognitoComponentMock;

    private static ApiClient anApiClient(String token) {
        return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
    }

    private static UserMapper userMapper = new UserMapper();

    public static Participant responsible1() {
        return userMapper.toRestParticipant(
                school.hei.haapi.model.User.
                        builder()
                        .id(TEACHER1_ID)
                        .ref("TCR21001")
                        .firstName("One")
                        .lastName("Teacher")
                        .build()
        );
    }

    public static Participant responsible2() {
        return userMapper.toRestParticipant(
                school.hei.haapi.model.User.
                        builder()
                        .id(TEACHER2_ID)
                        .ref("TCR21002")
                        .firstName("Two")
                        .lastName("Teacher")
                        .build()
        );
    }

    public static Event event1() {
        Event event = new Event();
        event.setId(EVENT1_ID);
        event.setName("EL1P3");
        event.setRef("event1_ref");
        event.setStartingDate(Instant.parse("2022-10-01T08:00:00.00Z"));
        event.setEndingDate(Instant.parse("2022-10-01T12:00:00.00Z"));
        event.setResponsible(responsible1());
        return event;
    }

    public static Event event2() {
        Event event = new Event();
        event.setId(EVENT2_ID);
        event.setName("FR");
        event.setRef("event2_ref");
        event.setStartingDate(Instant.parse("2022-10-01T08:00:00.00Z"));
        event.setEndingDate(Instant.parse("2022-10-01T12:00:00.00Z"));
        event.setResponsible(responsible2());
        return event;
    }

    public static Event someCreatableEvent() {
        Event event = new Event();
        event.setName("Some name");
        event.setRef(randomUUID() + "_ref");
        event.setStartingDate(Instant.parse("2022-10-01T08:00:00.00Z"));
        event.setEndingDate(Instant.parse("2022-10-01T12:00:00.00Z"));
        event.setResponsible(responsible2());
        return event;
    }

    @BeforeEach
    public void setUp() {
        setUpCognito(cognitoComponentMock);
    }

    @Test
    void badtoken_read_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        EventsApi api = new EventsApi(anonymousClient);
        assertThrowsForbiddenException(api::getEvents);
    }

    @Test
    void badtoken_write_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        EventsApi api = new EventsApi(anonymousClient);
        assertThrowsForbiddenException(() -> api.createOrUpdateEvents(List.of()));
    }

    @Test
    void student_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        EventsApi api = new EventsApi(student1Client);
        Event actual1 = api.getEventById(EVENT1_ID);
        List<Event> actualEvents = api.getEvents();

        assertEquals(event1(), actual1);
        assertTrue(actualEvents.contains(event1()));
        assertTrue(actualEvents.contains(event2()));
    }

    @Test
    void student_write_ko() {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        EventsApi api = new EventsApi(student1Client);
        assertThrowsForbiddenException(() -> api.createOrUpdateEvents(List.of()));
    }

    @Test
    void teacher_write_ok() throws ApiException {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);
        Event toCreate3 = someCreatableEvent();
        Event toCreate4 = someCreatableEvent();

        EventsApi api = new EventsApi(teacher1Client);
        List<Event> created = api.createOrUpdateEvents(List.of(toCreate3, toCreate4));

        assertEquals(2, created.size());
        Event created3 = created.get(0);
        assertTrue(isValidUUID(created3.getId()));
        toCreate3.setId(created3.getId());
        assertNotNull(created3.getStartingDate());
        toCreate3.setStartingDate(created3.getStartingDate());
        //
        assertEquals(created3, toCreate3);
        Event created4 = created.get(0);
        assertTrue(isValidUUID(created4.getId()));
        toCreate4.setId(created4.getId());
        assertNotNull(created4.getStartingDate());
        toCreate4.setStartingDate(created4.getStartingDate());
        assertEquals(created4, toCreate3);
    }

    @Test
    void manager_write_create_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        Event toCreate3 = someCreatableEvent();
        Event toCreate4 = someCreatableEvent();

        EventsApi api = new EventsApi(manager1Client);
        List<Event> created = api.createOrUpdateEvents(List.of(toCreate3, toCreate4));

        assertEquals(2, created.size());
        Event created3 = created.get(0);
        assertTrue(isValidUUID(created3.getId()));
        toCreate3.setId(created3.getId());
        assertNotNull(created3.getStartingDate());
        toCreate3.setStartingDate(created3.getStartingDate());
        //
        assertEquals(created3, toCreate3);
        Event created4 = created.get(0);
        assertTrue(isValidUUID(created4.getId()));
        toCreate4.setId(created4.getId());
        assertNotNull(created4.getStartingDate());
        toCreate4.setStartingDate(created4.getStartingDate());
        assertEquals(created4, toCreate3);
    }

    @Test
    void manager_write_update_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        EventsApi api = new EventsApi(manager1Client);
        List<Event> toUpdate = api.createOrUpdateEvents(List.of(
                someCreatableEvent(),
                someCreatableEvent()));
        Event toUpdate0 = toUpdate.get(0);
        toUpdate0.setName("A new name zero");
        Event toUpdate1 = toUpdate.get(1);
        toUpdate1.setName("A new name one");

        List<Event> updated = api.createOrUpdateEvents(toUpdate);

        assertEquals(2, updated.size());
        assertTrue(updated.contains(toUpdate0));
        assertTrue(updated.contains(toUpdate1));
    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
