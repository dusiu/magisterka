package pl.edu.uj.dusinski;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class DatabaseManagerConfiguration {

    @Bean
    public MongoTemplate mongoTemplate(@Value("${mongo.database.name}") String databaseName) {
        return new MongoTemplate(mongoDbFactory(databaseName));
    }

    private MongoDbFactory mongoDbFactory(String databaseName) {
        return new SimpleMongoDbFactory(new MongoClient(), databaseName);
    }

}
