package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProductCompositeController implements ProductComposite {

    // ---- Fields ----
    ProductCompositeService productCompositeService;

    // ---- Constructor ----
    @Autowired
    public ProductCompositeController(ProductCompositeService productCompositeService) {
        this.productCompositeService = productCompositeService;
    }

    // ---- Routes ----
    public ProductAggregate getProductByID(int prodID){

        return productCompositeService.getProductByID(prodID);
    }

    public ResponseEntity addProduct(@RequestBody Product product){

        System.out.println("Product: " + product);
        return productCompositeService.addProduct(product);
    };
}
