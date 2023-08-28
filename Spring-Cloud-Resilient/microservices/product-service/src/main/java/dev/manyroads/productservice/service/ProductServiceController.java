package dev.manyroads.productservice.service;

import com.mongodb.MongoWriteException;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.core.productservice.ProductService;
import dev.manyroads.api.productcomposite.TrackingID;
import dev.manyroads.productservice.persistence.ProductEntity;
import dev.manyroads.productservice.persistence.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import static java.lang.Thread.sleep;

@RestController
public class ProductServiceController implements ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;

    @Autowired
    public ProductServiceController(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // --- Routes ---

    /**
     * Find Product by product ID
     * @param prodID
     * @return
     */
    public Mono<Product> getProdByID(int prodID){

        System.out.println("Nu in Product service Controller, getProdByID, ProdID: " + prodID);
        Mono<Product> product = Mono.empty();

        try{
            Mono<ProductEntity> prodEntity = productRepository.findByProdID(prodID)
                    .switchIfEmpty(Mono.error(new Error("Product niet gevonden: " + prodID) ));

            product = prodEntity.map(p -> productMapper.entityToApi(p));

            //System.out.println("Product ontvangen service Controller, product: ");
            //        product.subscribe(System.out::println);

            // Simulation of slow response to trigger Circuit breaker in ProductCompositeService
            //sleep(2000);
        }catch (Exception ex){
            System.out.println("Foutje in ophalen Product/ProductServiceController: " + ex.getMessage() );
        }
        return product;
    }

    /**
     * Add a new product
     * @param product
     * @return
     */
    public Mono<Product> addProduct(@RequestBody Product product){

        System.out.println("In product Service addProduct");

        Mono<Product> addedProduct = Mono.empty();

        try{
            ProductEntity entity = productMapper.apiToEntity(product);
            addedProduct = productRepository.save(entity)
                    .map(e -> productMapper.entityToApi(e));
        }
        catch(MongoWriteException me){
            System.out.println("Foutje schrijven naar MongoDB: " + me.getMessage());
        }
        catch(Exception me){
            System.out.println("Alg Foutje schrijven naar MongoDB: " + me.getMessage());
        }

        return addedProduct;
    }
}
