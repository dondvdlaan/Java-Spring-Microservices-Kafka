package dev.manyroads.api.core.productservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface ProductService {

    @GetMapping(value = "/product/{prodID}",
            produces = "application/json"
    )
    public Product getProdByID(@PathVariable int prodID);

    @PostMapping(value = "/addProduct",
            produces = "application/json"
    )
    public ResponseEntity addProduct(@RequestBody Product product);
}
