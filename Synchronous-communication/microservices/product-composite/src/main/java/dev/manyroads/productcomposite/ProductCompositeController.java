package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProductCompositeController implements ProductComposite {

    // ---- Fields ----
    ProductCompositeService productCompositeService;

    // ---- Constructor ----
    //@Autowired // Autowire not needed when only 1 constructor is defined
    public ProductCompositeController(ProductCompositeService productCompositeService) {
        this.productCompositeService = productCompositeService;
    }

    // ---- Routes ----

    /**
     * Method to retriev an aggregated product based on product ID
     * @param prodID
     * @return
     */
    public ProductAggregate getProductByID(int prodID){

        return productCompositeService.getProductByID(prodID);
    }
}
