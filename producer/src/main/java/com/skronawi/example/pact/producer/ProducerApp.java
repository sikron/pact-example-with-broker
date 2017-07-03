package com.skronawi.example.pact.producer;

import com.skronawi.example.pact.producer.persistence.User;
import com.skronawi.example.pact.producer.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProducerApp {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }

    //just to have some dummy values for manual testing
    @Bean
    public CommandLineRunner commandLineRunner() {
        return strings -> {
            userRepository.save(new User(null, "veit"));
        };
    }
}
