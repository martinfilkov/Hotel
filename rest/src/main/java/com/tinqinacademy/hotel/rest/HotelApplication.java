package com.tinqinacademy.hotel.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.tinqinacademy.hotel.persistence.entity")
@EnableJpaRepositories(basePackages = "com.tinqinacademy.hotel.persistence.repositories")
@ComponentScan(basePackages = "com.tinqinacademy.hotel")
public class HotelApplication {
	public static void main(String[] args) {
		SpringApplication.run(HotelApplication.class, args);
	}
}
