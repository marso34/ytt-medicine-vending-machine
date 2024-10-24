package com.example.ytt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class YttApplication {

	public static void main(String[] args) {
		SpringApplication.run(YttApplication.class, args);
	}

}
