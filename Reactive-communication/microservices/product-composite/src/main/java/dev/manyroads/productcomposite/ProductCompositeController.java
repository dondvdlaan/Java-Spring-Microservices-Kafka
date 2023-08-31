package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productrecommendation.TestRecom;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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
        return "Holita";
    };
    // End Testing

    public Mono<ProductAggregate> getProductByID(int prodID){

        System.out.println("Controller getProductByID: " + prodID);
        return productCompositeService.getProdByID(prodID);
    }

    public Mono<Product> addProduct(@RequestBody Product product){

        System.out.println("Controller addProduct Product: " + product);
        return productCompositeService.addProduct(product);
    };

    public Mono<Recommendation> addRecommendation(@RequestBody Recommendation recommendation){

        System.out.println("Add Recommendation: " + recommendation);
        return productCompositeService.addRecommendation(recommendation);
    };

    public Mono<Void> addProduct(@RequestBody ProductAggregate product){

        System.out.println();
        System.out.println("Controller addProduct ProductAggregate: " + product);
        if(!product.getRecommendations().isEmpty()){
            product.getRecommendations().forEach(System.out::println);
        }
        // List to gather the feedback of different services
        List<Mono> monoList = new ArrayList<>();

        Product newProduct = new Product(
                product.getProdID(),
                product.getProdDesc(),
                product.getProdWeight(),
                "<noTrackingID>");
        System.out.println("Controller addProduct ProductAggregate: " + newProduct);

        try{
            Mono<Product> createdProduct = productCompositeService.addProduct(newProduct);
            System.out.println("Controller addProduct ProductAggregate createdProduct: " + createdProduct);

            monoList.add(createdProduct);

            if(!product.getRecommendations().isEmpty()){

                logger.debug("ProductCompositeController adding recommendations");
                product
                        .getRecommendations()
                        .stream()
                        .forEach(r-> {
                            logger.debug("ProductCompositeController recommendations: {}, r");
                            monoList.add(productCompositeService.addRecommendation(r));
                        });
            }
        }catch(Exception ex){
            System.out.println("Foutje creeren productAggregate: " + ex.getMessage());
        }
        // Check if Product and Recommendation creation succeeded TODO: check "monoList.toArray(new Mono[0])"
        Mono<Void> monoVoid = Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                .doOnError(ex -> System.out.println("createCompositeProduct failed: " + ex.toString()))
                .then();

        System.out.println("monoVoid: " + monoVoid);
        return monoVoid;
    };
}
