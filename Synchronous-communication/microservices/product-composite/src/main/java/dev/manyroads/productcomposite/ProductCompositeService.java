package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCompositeService {

    // ---- Constants ----
    final String DOUBLE_SLASH = "://";
    final String COLON = ":";

    // ---- Fields ----
    @Value("${PROD_SERVICE_PORT}")
    String productServicePort;
    @Value("${PROD_SERVICE_HOST}")
    String productServiceHost;
    String productRecommendationPort;
    String productRecommendationHost;
    String scheme;
    RestTemplate request;

    // ---- Constructor ----
    @Autowired
    public ProductCompositeService(@Value("${PROD_RECOMMENDATION_PORT}")
                                   String productRecommendationPort,
                                   @Value("${PROD_RECOMMENDATION_HOST}")
                                   String productRecommendationHost,
                                   @Value("${SCHEME}")
                                   String scheme,
                                   RestTemplate request) {
        this.productRecommendationPort = productRecommendationPort;
        this.productRecommendationHost = productRecommendationHost;
        this.scheme = scheme;
        this.request = request;
    }

    // --- Methods ----
    /**
     * Aggregate Product by retrieving ProductService and ProductRecommndation by prodID
     */
    protected ProductAggregate getProductByID(int prodID){

        /**
         * Get Product from ProductService by prodID
         */
        Product product = new Product();

        /**
         * Compose URI
         * Example http://localhost:7001/product/123
         */
        String productServiceIP = scheme + DOUBLE_SLASH + productServiceHost + COLON + this.productServicePort;
        String productPath = "/product/";
        String productServiceURL = productServiceIP + productPath + prodID;
        System.out.println("productServiceURL: " + productServiceURL);

        try{
            //ResponseEntity<Product> res = request.getForEntity(productServiceURL,Product.class);
            //System.out.println("res.getBody(): " + res.getBody());
            product = request.getForEntity(productServiceURL,Product.class).getBody();
        }
        catch(HttpClientErrorException ex){
            System.out.println("Error getProductService:" + ex.getMessage());
        }

        /**
         * Get recommendations from ProductRecommendation by prodID
         */
        List<Recommendation> recommendations = new ArrayList<>();

        // Compose URI
        String productRecommendationIP = scheme + DOUBLE_SLASH + productRecommendationHost + COLON + productRecommendationPort;
        String recommendationPath = "/recommendation";
        String productQuery = "?prodID=";
        String productRecommendationURL =
                productRecommendationIP +
                recommendationPath +
                productQuery +
                prodID;
        System.out.println("productRecommendationURL: " + productRecommendationURL);

        try{
             recommendations = request.exchange(
                            productRecommendationURL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<Recommendation>>() {} )
                    .getBody();
        }
        catch(HttpClientErrorException ex){
            System.out.println("Error getProductRecommendation:" + ex.getMessage());
        }
        /*
        for(Recommendation r : recommendations){
            System.out.println("r: " + r);
        }
                 */
        /**
         * Aggregate product and recommendations
         */
        ProductAggregate productAggregate = new ProductAggregate();
        productAggregate.setProdID(product.getProdID());
        productAggregate.setProdDesc(product.getProdDesc());
        productAggregate.setProdWeight(product.getProdWeight());
        productAggregate.setTrackingID(product.getTrackingID());
        productAggregate.setRecommendations(recommendations);

        return productAggregate;
    }
}
