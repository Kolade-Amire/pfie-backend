package com.kay.pfie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.amqp.*;
@SpringBootApplication
public class PfieBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PfieBackendApplication.class, args);
	}

}
