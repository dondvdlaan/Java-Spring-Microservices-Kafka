package dev.manyroads.api.productcomposite;

import dev.manyroads.api.core.productservice.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProductComposite {

    @GetMapping(
            value = "/composite/{prodID}",
            produces = "application/json"
    )
    public ProductAggregate getProductByID(@PathVariable int prodID);

    @PostMapping(value = "/composite/addProduct",
            produces = "application/json"
    )
    public ResponseEntity addProduct(@RequestBody ProductAggregate product);
}
