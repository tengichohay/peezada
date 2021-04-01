package com.example.springsocial;

import com.example.springsocial.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
@CrossOrigin(value = "http://localhost:3000")

public class SpringSocialApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSocialApplication.class, args);
	}
}
