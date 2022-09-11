package school.hei.haapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.haapi.model.Place;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, String> {
    Optional<Place> getPlaceByLabel(String label);
}
