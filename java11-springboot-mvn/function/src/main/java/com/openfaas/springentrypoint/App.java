package com.openfaas.springentrypoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@SpringBootApplication
//@EnableAutoConfiguration
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity handle(@RequestBody byte[] payload) {
        String response = "Hello, " + new String(payload);//handler.handle(payload);

        logger.info(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}