package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // ---- Logger ----
    private static final Logger logger = LoggerFactory.getLogger(ProductCompositeService.class);

    // ---- Constants ----
    static final String DOUBLE_SLASH = "://";
    static final String COLON = ":";

    // ---- Fields ----
    @Value("${PROD_SERVICE_PORT}")
    String productServicePort;
    @Value("${PROD_SERVICE_HOST}")
    String productServiceHost;
    final String productRecommendationPort;
    final String productRecommendationHost;
    final String scheme;
    final RestTemplate request;

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
     * Aggregate Product by retrieving synchronously Product and ProductRecommndation by prodID
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

        logger.debug("getProductByID -> productServiceURL: {} ", productServiceURL );

        try{
            //ResponseEntity<Product> res = request.getForEntity(productServiceURL,Product.class);
            //System.out.println("res.getBody(): " + res.getBody());
            product = request.getForEntity(productServiceURL,Product.class).getBody();
        }
        catch(HttpClientErrorException ex){
            logger.error("getProductService: {}", ex.getMessage());
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

        logger.debug("getProductByID -> productRecommendationURL: {} ", productRecommendationURL );

        try{
             recommendations = request.exchange(
                            productRecommendationURL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<Recommendation>>() {} )
                    .getBody();
        }
        catch(HttpClientErrorException ex){
            logger.error("getProductRecommendation: {}", ex.getMessage());
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
