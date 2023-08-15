package dev.manyroads.api.productcomposite;

import dev.manyroads.api.core.productservice.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductComposite {

    @GetMapping(
            value = "/composite/{prodID}",
            produces = "application/json"
    )
    public ProductAggregate getProductByID(@PathVariable int prodID);

}
