package com.skronawi.example.pact.producer;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.skronawi.example.pact.producer.persistence.User;
import com.skronawi.example.pact.producer.persistence.UserRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

@RunWith(PactRunner.class) //the junit test runner
@Provider("producer_provider") //so that pact knows, which pact files should be verified
@VerificationReports("console")
//@PactFolder("/home/simon/dev/workspaces/java/pact-demo-parent/consumer/target/pacts")
@PactBroker(host = "localhost", port = "80", authentication = @PactBrokerAuth(username = "pact", password = "pact"))
public class ProducerPactIT {

    private static ConfigurableApplicationContext context;

    @TestTarget
    public final Target target = new HttpTarget(8080);

    @BeforeClass
    public static void startSpring() {
        context = SpringApplication.run(ProducerApp.class);
    }

    @AfterClass
    public static void kill() {
        context.stop();
    }

    @State("1 user")
    public void withSimon() {
        UserRepository userRepository = (UserRepository) context.getBean("userRepository");
        Optional<User> simon = userRepository.findAll().stream().filter(user -> user.getUsername().equals("veit")).findFirst();
        if (!simon.isPresent()) {
            userRepository.save(new User(null, "veit"));
        }
        System.out.println("1 user exist");
    }

    @State("no users")
    public void withoutUsers() {
        UserRepository userRepository = (UserRepository) context.getBean("userRepository");
        userRepository.findAll().forEach(user -> userRepository.delete(user));
        System.out.println("all users deleted");
    }
}
