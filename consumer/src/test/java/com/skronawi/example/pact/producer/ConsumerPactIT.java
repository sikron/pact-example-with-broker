package com.skronawi.example.pact.producer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConsumerPactIT {

    /*
     starts the mock producer as specified.
     */
    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(
            "producer_provider", "localhost", 8080, this);

    /*
     the pact framework parametrizes the mock producer to act like this. the result of all fragments will be a json
     file.

     the "provider" is necessary so that later a provider knows, what pact files are meant for him. this will be part
     of the file-name later, i.e. here it will be "test_consumer-producer_provider.json".

     the "given" specifies the states, in which the provider should be in. this matches the @State annotated methods
     later in the provider verifications.
     */
    @Pact(provider = "producer_provider", consumer = "test_consumer")
    public RequestResponsePact getDefaultUserFragment(PactDslWithProvider builder) {
        PactDslJsonArray usersJson = new PactDslJsonArray()
                .object()
                .stringValue("username", "veit")
                .uuid("id")
                .closeObject()
                .asArray();
        return builder
                .given("1 user")
                .uponReceiving("get users containing 1 user and existing ids")
                .path("/")
                .method(HttpMethod.GET.name())
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body(usersJson)
                .toPact();
    }

    @Pact(provider = "producer_provider", consumer = "test_consumer")
    public RequestResponsePact getNoUsersFragment(PactDslWithProvider builder) {
        return builder
                .given("no users")
                .uponReceiving("get empty array")
                .path("/")
                .method(HttpMethod.GET.name())
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body(new PactDslJsonArray().asArray())
                .toPact();
    }

    /*
     a normal failsafe test. the pact annotation specifies, how pact should parametrize the mock provider for this
     test now.
     */
    @Test
    @PactVerification(fragment = "getDefaultUserFragment")
    public void testGetUsersDirectlyFromProvider() {

        ResponseEntity<Set> usersResponse = new RestTemplate().getForEntity(
                URI.create("http://localhost:8080"), Set.class);
        Set users = usersResponse.getBody();

        Set<String> userNames = new HashSet<>();
        for (Object user : users) {
            Map userAsMap = (Map) user;
            userNames.add((String) userAsMap.get("username"));
        }

        Assert.assertEquals(1, userNames.size());
        Assert.assertTrue(userNames.contains("veit"));
    }

    /*
     tests against the client/consumer, which internally calls the producer.
     */
    @Test
    @PactVerification(fragment = "getDefaultUserFragment")
    public void testGetUsersViaConsumer() {

        ResponseEntity<Set> usersResponse = new RestTemplate().getForEntity(
                URI.create("http://localhost:8081"), Set.class);
        Set userNames = usersResponse.getBody();

        Assert.assertEquals(1, userNames.size());
        Assert.assertTrue(userNames.contains("veit"));
    }

    @Test
    @PactVerification(fragment = "getNoUsersFragment")
    public void testGetNoUsersViaConsumer() {

        ResponseEntity<Set> usersResponse = new RestTemplate().getForEntity(
                URI.create("http://localhost:8081"), Set.class);
        Assert.assertTrue(usersResponse.getBody().isEmpty());
    }
}
