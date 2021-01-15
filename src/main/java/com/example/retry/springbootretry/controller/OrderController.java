package com.example.retry.springbootretry.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private RestTemplate restTemplate;

    private int attempts=1;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/order")
    @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> createOrder(){
        logger.info("order method called:::"+ attempts++);
        String response = restTemplate.getForObject("http://localhost:8081/item", String.class);
        logger.info("item service called:::");

        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @Recover
    public ResponseEntity<String> orderFallback(Exception e){
        attempts=0;
        return new ResponseEntity<String>("Item service is down", HttpStatus.INTERNAL_SERVER_ERROR);

    }





}
