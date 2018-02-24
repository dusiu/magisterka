package pl.edu.uj.dusinski.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TestService {

    @Autowired
    private TestRepository testRepository;

//    @PostConstruct
    public void test() {
        testRepository.save(new Test("nikodem", 22));
        testRepository.findAll().forEach(System.out::println);
    }

}
