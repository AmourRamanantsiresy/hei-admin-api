package school.hei.haapi.model.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.haapi.model.Course;
import school.hei.haapi.model.exception.BadRequestException;

import javax.xml.validation.Validator;
import java.util.function.Consumer;

@Component
@AllArgsConstructor
public class CourseValidator implements Consumer<Course> {

    @Override
    public void accept(Course course) {
        if(course.getCredits() <= 0){
            throw new BadRequestException("Credits cannot be null or negative");
        }else if(course.getTotal_hours() <= 0){
            throw new BadRequestException("Total hours cannot be null or negative");
        }else if(course.getName().isEmpty() || course.getName().isBlank()){
            throw new BadRequestException("Name is mandatory");
        }else if(course.getRef().isEmpty() || course.getRef().isBlank()){
            throw new BadRequestException("Ref is mandatory");
        }
    }

    @Override
    public Consumer<Course> andThen(Consumer<? super Course> after) {
        return Consumer.super.andThen(after);
    }
}
