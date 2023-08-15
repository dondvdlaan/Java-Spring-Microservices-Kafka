package dev.manyroads.productcomposite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ProductCompositeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCompositeApplication.class, args);
	}

	@Bean
	RestTemplate request(){
		return new RestTemplate();
	}
/*
	@Bean
	public OpenApi getOpenApiDocumentation() {
		return new OpenApi()
				.info(new Info().title(apiTitle))
				.description(apiDescription)
				.version(apiVersion)
				.contact(new Contact)
				.build();
	}

 */
}
