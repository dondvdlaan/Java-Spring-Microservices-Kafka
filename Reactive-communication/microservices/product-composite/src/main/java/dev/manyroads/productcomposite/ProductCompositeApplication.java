package dev.manyroads.productcomposite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
	Scheduler usable by publishOn or subscribeOn
	Optimized for longer executions, an alternative for blocking tasks
	where the number of active tasks (and threads) is capped
	 */
	@Bean
	public Scheduler publishEventScheduler() {

		System.out.println("*** In publishEventScheduler ***");
		return Schedulers.newBoundedElastic(3, 3, "publish-pool");
	}
}
