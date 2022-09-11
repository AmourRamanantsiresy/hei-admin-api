package school.hei.haapi.integration;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.hei.haapi.SentryConf;
import school.hei.haapi.endpoint.rest.api.TeachingApi;
import school.hei.haapi.endpoint.rest.client.ApiClient;
import school.hei.haapi.endpoint.rest.client.ApiException;
import school.hei.haapi.endpoint.rest.model.Course;
import school.hei.haapi.endpoint.rest.security.cognito.CognitoComponent;
import school.hei.haapi.integration.conf.AbstractContextInitializer;
import school.hei.haapi.integration.conf.TestUtils;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static school.hei.haapi.integration.conf.TestUtils.BAD_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.COURSE1_ID;
import static school.hei.haapi.integration.conf.TestUtils.COURSE2_ID;
import static school.hei.haapi.integration.conf.TestUtils.MANAGER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.STUDENT1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.TEACHER1_TOKEN;
import static school.hei.haapi.integration.conf.TestUtils.anAvailableRandomPort;
import static school.hei.haapi.integration.conf.TestUtils.assertThrowsForbiddenException;
import static school.hei.haapi.integration.conf.TestUtils.isValidUUID;
import static school.hei.haapi.integration.conf.TestUtils.setUpCognito;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = CourseIT.ContextInitializer.class)
@AutoConfigureMockMvc
public class CourseIT {

    @MockBean
    private SentryConf sentryConf;

    @MockBean
    private CognitoComponent cognitoComponentMock;

    private static ApiClient anApiClient(String token) {
        return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
    }

    public static Course course1() {
        Course course = new Course();
        course.setId(COURSE1_ID);
        course.setName("Name of the course 1");
        course.setRef("PROG1");
        course.setCredits(5);
        course.setTotalHours(10);
        return course;
    }

    public static Course course2() {
        Course course = new Course();
        course.setId(COURSE2_ID);
        course.setName("Name of the course 2");
        course.setRef("PROG2");
        course.setCredits(5);
        course.setTotalHours(10);
        return course;
    }

    public static Course someCreatableCourse() {
        Course course = new Course();
        course.setName("Some name");
        course.setRef("COURSE22-" + randomUUID());
        course.setCredits(5);
        course.setTotalHours(10);
        return course;
    }

    @BeforeEach
    public void setUp() {
        setUpCognito(cognitoComponentMock);
    }

    @Test
    void badtoken_read_ko() {
        ApiClient anonymousClient = anApiClient(BAD_TOKEN);

        TeachingApi api = new TeachingApi(anonymousClient);
        assertThrowsForbiddenException(api::getCourses);
    }

    @Test
    void student_read_ok() throws ApiException {
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        TeachingApi api = new TeachingApi(student1Client);
        Course actual1 = api.getCourseById(COURSE1_ID);
        List<Course> actualCourses = api.getCourses();

        assertEquals(course1(), actual1);
        assertTrue(actualCourses.contains(course1()));
        assertTrue(actualCourses.contains(course2()));
    }

    @Test
    void student_write_ko(){
        ApiClient student1Client = anApiClient(STUDENT1_TOKEN);

        TeachingApi api = new TeachingApi((student1Client));
        assertThrowsForbiddenException(() -> api.createOrUpdateCourses(new Course()));
    }

    @Test
    void teacher_read_ok() throws ApiException {
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);

        TeachingApi api = new TeachingApi((teacher1Client));
        Course actual1 = api.getCourseById(COURSE1_ID);
        List<Course> actualCourses = api.getCourses();

        assertEquals(course1(), actual1);
        assertTrue(actualCourses.contains(course1()));
        assertTrue(actualCourses.contains(course2()));
    }

    @Test
    void teacher_write_ko(){
        ApiClient teacher1Client = anApiClient(TEACHER1_TOKEN);

        TeachingApi api = new TeachingApi((teacher1Client));
        assertThrowsForbiddenException(() -> api.createOrUpdateCourses(new Course()));
    }

    @Test
    void manager_write_create_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);
        Course toCreate3 = someCreatableCourse();
        Course toCreate4 = someCreatableCourse();

        TeachingApi api = new TeachingApi(manager1Client);
        Course created3 = api.createOrUpdateCourses(toCreate3);
        Course created4 = api.createOrUpdateCourses(toCreate4);

        assertTrue(isValidUUID(created3.getId()));
        toCreate3.setId(created3.getId());
        assertNotNull(created3.getCredits());
        toCreate3.setCredits(created3.getCredits());
        //
        assertEquals(created3, toCreate3);
        assertTrue(isValidUUID(created4.getId()));
        toCreate4.setId(created4.getId());
        assertNotNull(created4.getCredits());
        toCreate4.setCredits(created4.getCredits());
        assertEquals(created4, toCreate3);
    }

    @Test
    void manager_write_update_ok() throws ApiException {
        ApiClient manager1Client = anApiClient(MANAGER1_TOKEN);

        TeachingApi api = new TeachingApi(manager1Client);
        Course toUpdate = api.createOrUpdateCourses(someCreatableCourse());
        toUpdate.setName("A new name zero");

        Course updated = api.createOrUpdateCourses(toUpdate);

        assertTrue(updated.equals(toUpdate));
    }

    static class ContextInitializer extends AbstractContextInitializer {
        public static final int SERVER_PORT = anAvailableRandomPort();

        @Override
        public int getServerPort() {
            return SERVER_PORT;
        }
    }
}
