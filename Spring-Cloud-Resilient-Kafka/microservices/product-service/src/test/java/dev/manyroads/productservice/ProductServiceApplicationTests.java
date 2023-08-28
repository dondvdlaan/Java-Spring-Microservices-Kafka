package dev.manyroads.productservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;


@SpringBootTest
class ProductServiceApplicationTests {

	@Test
	void testFlux() {

		List<Integer> fluxie = Flux.just(1,2,3,4)
				.filter(i -> i % 2 == 0)
				.map(i -> i * 2)
				.log()
				.collectList()
				.block();

		fluxie.forEach(System.out::println);

		Assertions.assertEquals(fluxie,List.of(4,8));

	}

}
