package school.hei.haapi.endpoint.rest.mapper;

import org.springframework.stereotype.Component;
import school.hei.haapi.model.Place;

@Component
public class PlaceMapper {

    public school.hei.haapi.endpoint.rest.model.Place toRest(Place place) {
        var restPlace = new school.hei.haapi.endpoint.rest.model.Place();
        restPlace.setId(place.getId());
        restPlace.setLabel(place.getLabel());
        return restPlace;
    }

    public Place toDomain(school.hei.haapi.endpoint.rest.model.Place restPlace) {
        return Place.builder()
                .id(restPlace.getId())
                .label(restPlace.getLabel())
                .build();
    }
}
