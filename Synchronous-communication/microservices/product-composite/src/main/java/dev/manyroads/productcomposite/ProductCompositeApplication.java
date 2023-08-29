package dev.manyroads.productcomposite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ProductCompositeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCompositeApplication.class, args);
	}

	/*
	Defining RestTemplate type
	 */
	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
