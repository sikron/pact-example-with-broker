package com.skronawi.example.pact.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
public class ConsumerController {

    @Value("${producer.url}")
    private String producerUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public Collection<String> getUsernames() {

        ResponseEntity<HashSet> usersResponse = restTemplate.getForEntity(URI.create(producerUrl), HashSet.class);
        HashSet users = usersResponse.getBody();

        Set<String> usernames = new HashSet<>();
        for (Object user : users) {
            Map userAsMap = (Map) user;
            usernames.add((String) userAsMap.get("username"));
        }
        return usernames;
    }
}
