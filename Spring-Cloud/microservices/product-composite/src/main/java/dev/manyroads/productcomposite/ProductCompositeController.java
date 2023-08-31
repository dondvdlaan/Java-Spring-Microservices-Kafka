package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductCompositeController implements ProductComposite {

    // ---- Logger ----
    final static Logger logger = LoggerFactory.getLogger(ProductCompositeController.class);

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

        System.out.println("Controller getProductByID: " + prodID);
        return productCompositeService.getProdByID(prodID);
    }

    public Mono<Void> addProduct(@RequestBody ProductAggregate product){

        logger.debug("Controller addProduct Product: {}", product);

        // Coolect feedback from different services
        List<Mono> monoList = new ArrayList<>();

        Product newProduct = new Product(
                product.getProdID(),
                product.getProdDesc(),
                product.getProdWeight(),
                "noTrackingIDForNow");
        try{
            // Add product
            monoList.add(productCompositeService.addProduct(product));

            // Add recommendations
            if(!product.getRecommendations().isEmpty()){
                product.getRecommendations()
                        .forEach(r -> {
                            logger.debug("Adding recommendation: {}", r);
                            monoList.add(productCompositeService.addRecommendation(r));
                        });
            }
        }catch (Exception ex){
            logger.error("Failure adding product: {}",ex.getMessage());
        }

        return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                .doOnError(ex -> logger
                        .warn("ProductCompositeController adProduct failed: {}", ex.toString()))
                .then();
    }

    public Mono<Recommendation> addRecommendation(@RequestBody Recommendation recommendation){

        System.out.println("Add Recommendation: " + recommendation);
        return productCompositeService.addRecommendation(recommendation);
    };
}
