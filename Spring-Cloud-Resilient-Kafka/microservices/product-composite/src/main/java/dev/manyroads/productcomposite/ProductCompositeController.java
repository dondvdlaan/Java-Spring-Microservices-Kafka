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

import java.util.ArrayList;
import java.util.List;

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

    public Mono<Void> addProduct(@RequestBody ProductAggregate productAggregate){

        System.out.println("In ProductCompositeController");
        System.out.println("In ProductAggregate: " + productAggregate);

        // Create a list of Monos for collecting the returns of different services
        List<Mono> returnMonos  = new ArrayList<>();
        try {
            Product product = new Product(
                    productAggregate.getProdID(),
                    productAggregate.getProdDesc(),
                    productAggregate.getProdWeight(),
                    "nothingToSee");

            Mono monoProduct = productCompositeService.addProduct(product);
            returnMonos.add(monoProduct);

            List<Recommendation> recommendations = new ArrayList<>(productAggregate.getRecommendations());

            Mono monoRecommendation = Mono.empty();
            for (Recommendation r : recommendations) {
                System.out.println("Controller addProduct recommendation: " + r);
                monoRecommendation = productCompositeService.addRecommendation(r);
                returnMonos.add(monoRecommendation);
            }
        }
        catch(Exception ex){
                System.out.println("Foutje toevoegen product" + ex.getMessage());
        }
        // TODO: find out working of "(r -> "", returnMonos.toArray(new Mono[0])"
        return Mono.zip(r -> "", returnMonos.toArray(new Mono[0]))
                .doOnError(ex -> System.out.println("createCompositeProduct failed: {}" + ex.getMessage()))
                .then();
    };

    public Mono<Recommendation> addRecommendation(@RequestBody Recommendation recommendation){

        System.out.println("Add Recommendation: " + recommendation);
        return productCompositeService.addRecommendation(recommendation);
    };
}
