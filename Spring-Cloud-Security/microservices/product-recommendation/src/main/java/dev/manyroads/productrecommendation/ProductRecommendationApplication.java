package dev.manyroads.productrecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class ProductRecommendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(dev.manyroads.productrecommendation.ProductRecommendationApplication.class, args);
	}

	/*
	Scheduler usable by publishOn or subscribeOn
	Optimized for longer executions, an alternative for blocking tasks
	where the number of active tasks (and threads) is capped
	 */
	@Bean
	public Scheduler JDBCScheduler() {

		System.out.println("*** In JDBCScheduler ***");
		return Schedulers.newBoundedElastic(3, 3, "JDBC-pool");
	}
}
