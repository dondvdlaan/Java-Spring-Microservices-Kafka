package dev.manyroads.api.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productrecommendation.TestRecom;
import dev.manyroads.api.core.productservice.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface ProductComposite {

    // Testing
    @GetMapping("/test")
    public String testing();
    // End Testing

    @GetMapping(
            value = "/composite/{prodID}",
            produces = "application/json"
    )
    public Mono<ProductAggregate> getProductByID(@PathVariable int prodID);
    //public ProductAggregate getProductByID(@PathVariable int prodID);

    @PostMapping(value = "/addProduct",
            produces = "application/json"
    )
    public Mono<Product> addProduct(@RequestBody Product product);

    @PostMapping(value = "/addRecommendation",
            produces = "application/json"
    )
    public Mono<Recommendation> addRecommendation(@RequestBody Recommendation recommendation);

    @PostMapping(value = "/addProductAggregate",
            produces = "application/json"
    )
    public Mono<Void> addProduct(@RequestBody ProductAggregate product);

}
