package dev.manyroads.productservice;

import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.core.productservice.ProductService;
import dev.manyroads.api.productcomposite.TrackingID;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductServiceController implements ProductService {

    public Product getProdByID(int prodID){
        return new Product(prodID,"Kaas",123D,new TrackingID(prodID).toString());
    }
}
