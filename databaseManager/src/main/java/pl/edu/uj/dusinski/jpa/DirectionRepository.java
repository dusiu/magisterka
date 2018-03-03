package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;

import java.util.List;

public interface DirectionRepository extends MongoRepository<Direction, String> {

    List<Direction> findAllByAirline(Airline airline);

    List<Direction> findByFromCode(String fromCode);

    Direction findByFromCodeAndToCode(String fromCode, String toCode);

}
