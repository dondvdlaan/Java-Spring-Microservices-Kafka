package dev.manyroads.api.core.productservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    @GetMapping(value = "/product/{prodID}",
            produces = "application/json"
    )
    public Mono<Product> getProdByID(@PathVariable int prodID);
    //public Mono<Product> getProdByID(@PathVariable int prodID);

    @PostMapping(value = "/addProduct",
            produces = "application/json"
    )
    public Mono<Product> addProduct(@RequestBody Product product);
}
