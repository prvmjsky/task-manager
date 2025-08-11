package hexlet.code.util;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;

public class DataFaker {

    @Bean
    public Faker getFaker() {
        return new Faker();
    }
}
