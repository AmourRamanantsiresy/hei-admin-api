package school.hei.haapi.model.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.haapi.model.Place;
import school.hei.haapi.model.exception.BadRequestException;

import java.util.function.Consumer;

@Component
@AllArgsConstructor
public class PlaceValidator implements Consumer<Place> {
    @Override
    public void accept(Place place) {
        if (place.getLabel().isEmpty() || place.getLabel().isBlank()) {
            throw new BadRequestException("Label is mandatory");
        }
        accept(place);
    }

    @Override
    public Consumer<Place> andThen(Consumer<? super Place> after) {
        return Consumer.super.andThen(after);
    }
}
