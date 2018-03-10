package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.AirportDetails;

import java.util.List;

public interface AirportDetailsRepository extends MongoRepository<AirportDetails, String> {

    List<AirportDetails> findByCode(String code);

}
