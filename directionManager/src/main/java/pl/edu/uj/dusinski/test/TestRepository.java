package pl.edu.uj.dusinski.test;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.uj.dusinski.test.Test;

public interface TestRepository extends MongoRepository<Test, Long> {

}
