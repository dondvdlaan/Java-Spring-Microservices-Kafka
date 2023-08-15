package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productrecommendation.TestRecom;
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
import reactor.core.publisher.Mono;

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
    // Testing
    public String testing(){

        System.out.println("Testing");
        return "Hilita";
    };
    // End Testing

    public Mono<ProductAggregate> getProductByID(int prodID){
    //public ProductAggregate getProductByID(int prodID){

        System.out.println("Controller getProductByID: " + prodID);
        return productCompositeService.getProdByID(prodID);
        //return productCompositeService.getProductByID(prodID);
    }

    public Mono<Product> addProduct(@RequestBody Product product){

        System.out.println("Controller addProduct Product: " + product);
        return productCompositeService.addProduct(product);
    };

    public Mono<Recommendation> addRecommendation(@RequestBody Recommendation recommendation){

        System.out.println("Add Recommendation: " + recommendation);
        return productCompositeService.addRecommendation(recommendation);
    };
}
