package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.FlightDetails;

public interface FlightDetailsRepository extends MongoRepository<FlightDetails, String> {
}
