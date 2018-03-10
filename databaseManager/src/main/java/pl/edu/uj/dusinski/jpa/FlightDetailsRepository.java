package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightDetailsRepository extends MongoRepository<FlightDetails, String> {

    List<FlightDetails> findByDirectionIn(List<Direction> directions);

    Optional<FlightDetails> findTopByDirectionInAndFlyDateBetweenOrderByOriginalPrice(List<Direction> direction, LocalDate start, LocalDate end);

}
