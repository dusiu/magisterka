package pl.edu.uj.dusinski.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;

public interface DirectionRefreshDetailsRepository extends MongoRepository<DirectionRefreshDetails, String> {

    DirectionRefreshDetails findTopByAirlineOrderByIdDesc(Airline airline);
}
