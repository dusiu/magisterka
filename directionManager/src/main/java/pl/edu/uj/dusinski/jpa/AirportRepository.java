package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.AirportDetails;

public interface AirportRepository extends MongoRepository<AirportDetails, String> {
}
