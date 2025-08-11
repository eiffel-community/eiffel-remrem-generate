package com.ericsson.eiffel.remrem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class OpenApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(OpenApiApplication.class, args);
	}
}
