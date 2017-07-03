# PACT Demo

This demo shows, how PACT can be used to assert the contract (REST API) between a client and a service. PACT is client
driven, i.e. the client specifies the expected behaviour and generates a according JSON file. This is usually done by
writing tests incl. according generation of the file. The corresponding service then can be automatically verified against
this pact file. In order to make these pact files accessible they are uploaded to and pulled from a "pact broker".

## Instructions

0. start the broker

  - docker and docker-compose is needed
  - `cd pact-broker && sudo docker-compose up`
  - web UI under `localhost:80`

1. generate pact file via junit test

  - `cd consumer && mvn clean install -DskipTests=false`
  - this will generate a pact JSON file in `consumer/target/pacts`
  
2. upload generated pact file to broker

  - `cd consumer && mvn pact:publish`

3. verify pact against producer

  - `cd producer && mvn clean install -DskipTests=false`
  
4. have a look at `http://localhost:80`

  - overview of pacts
  - differences between different version of 1 pact
  - overview of relations of services via a graph
  
  
## Resources

  - general info
    - https://www.javacodegeeks.com/2017/03/consumer-driven-testing-pact-spring-boot.html
    - https://github.com/DiUS/pact-workshop-jvm
    
  - consumer side
    - lib for client testing (and annotations): https://github.com/DiUS/pact-jvm/tree/master/pact-jvm-consumer-junit

  - producer side
    - lib for producer verifications (and annotations): https://github.com/DiUS/pact-jvm/tree/master/pact-jvm-provider-junit
    
  - broker
    - https://github.com/DiUS/pact_broker
    - https://github.com/DiUS/pact_broker-docker
    
  - maven plugin for consumer and producer (upload of files to broker and download for verification)
    - https://github.com/DiUS/pact-jvm/tree/master/pact-jvm-provider-maven
