package school.hei.haapi.endpoint.rest.mapper;


import org.springframework.stereotype.Component;
import school.hei.haapi.model.Course;

@Component
public class CourseMapper {

    public school.hei.haapi.endpoint.rest.model.Course toRest(Course course) {
        var restCourse = new school.hei.haapi.endpoint.rest.model.Course();
        restCourse.setId(course.getId());
        restCourse.setName(course.getName());
        restCourse.setRef(course.getRef());
        restCourse.setTotalHours(course.getTotal_hours());
        restCourse.setCredits(course.getCredits());
        return restCourse;
    }

    public Course toDomain(school.hei.haapi.endpoint.rest.model.Course restCourse) {
        return Course.builder()
                .id(restCourse.getId())
                .name(restCourse.getName())
                .ref(restCourse.getRef())
                .total_hours(restCourse.getTotalHours())
                .credits(restCourse.getCredits())
                .build();
    }
}

