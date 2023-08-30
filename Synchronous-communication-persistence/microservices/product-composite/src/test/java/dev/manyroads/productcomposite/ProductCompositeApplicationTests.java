package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductCompositeApplicationTests {

	final int PRODUCT_ID_OK = 123;
	final int PRODUCT_RECOMM_ID_OK = 456;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductCompositeService productCompositeService;

	@BeforeEach
	void setUp() {

		when(productCompositeService.getProductByID(PRODUCT_ID_OK))
				.thenReturn(new ProductAggregate(PRODUCT_ID_OK,
						"name",
						1D,
						"mock-address",
						List.of(new Recommendation(PRODUCT_ID_OK,PRODUCT_RECOMM_ID_OK,"Moet je kopen"))));
	}
	@Test
	void contextLoads() {
	}
	@Test
	void getProductByID() {
		client.get()
				// Forcing  400 BAD_REQUEST error by entering String instead of int
				//.uri("/composite/abc")
				.uri("/composite/" + PRODUCT_ID_OK)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
					.jsonPath("$.prodID").isEqualTo(PRODUCT_ID_OK)
					.jsonPath("$.recommendations.length()").isEqualTo(1);
	}

}
