package dev.manyroads.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	/*
	To be able to look up microservices available through the Eureka server, a WebClient.Builder is
	created including the Spring Loadbalancer. Type WebClient.Builder is used in ProductCompositeService
	to construct the property webClient.
	 */
	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder(){
		return WebClient.builder();
	}

}
