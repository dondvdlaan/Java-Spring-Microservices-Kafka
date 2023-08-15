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
    public Product getProdByID(int prodID){

        System.out.println("Nu in Product service Controller, ProdID: " + prodID);

        return new Product(prodID,"Kaas",123D,new TrackingID(prodID).toString());
    }

    public ResponseEntity<Product> addProduct(@RequestBody Product product){

        System.out.println("In product Service addProduct");

        try{
            ProductEntity prodEntity = productMapper.apiToEntity(product);
            productRepository.save(prodEntity);
        }
        catch(MongoWriteException me){
            System.out.println("Foutje schrijven naar MongoDB: " + me.getMessage());
        }
        catch(Exception me){
            System.out.println("Alg Foutje schrijven naar MongoDB: " + me.getMessage());
        }

        return ResponseEntity.ok().body(product);
    }
}
