package school.hei.haapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.haapi.model.Course;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> getCourseByName(String name);
}
