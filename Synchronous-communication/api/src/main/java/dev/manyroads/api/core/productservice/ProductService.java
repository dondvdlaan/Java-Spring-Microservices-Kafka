package dev.manyroads.api.core.productservice;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

public interface ProductService {

    @GetMapping(value = "/product/{prodID}",
            produces = "application/json"
    )
    public Product getProdByID(@PathVariable int prodID);
}
