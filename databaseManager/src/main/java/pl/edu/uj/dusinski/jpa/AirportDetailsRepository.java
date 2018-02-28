package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.AirportDetails;

public interface AirportDetailsRepository extends MongoRepository<AirportDetails, String> {

    AirportDetails findByCode(String code);

}
