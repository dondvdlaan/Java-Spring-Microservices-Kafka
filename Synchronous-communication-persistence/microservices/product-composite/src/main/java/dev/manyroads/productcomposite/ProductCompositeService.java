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
    final String productServicePort;
    final String productServiceHost;
    final String productRecommendationPort;
    final String productRecommendationHost;
    final String scheme;
    final String productServiceIP;
    final String recommendationIP;
    final RestTemplate request;

    // ---- Constructor ----
    @Autowired
    public ProductCompositeService(
            @Value("${PROD_SERVICE_PORT}")
            String productServicePort,
            @Value("${PROD_SERVICE_HOST}")
            String productServiceHost,
            @Value("${PROD_RECOMMENDATION_PORT}")
            String productRecommendationPort,
            @Value("${PROD_RECOMMENDATION_HOST}")
            String productRecommendationHost,
            @Value("${SCHEME}")
            String scheme,
            RestTemplate request) {

        this.productRecommendationPort  = productRecommendationPort;
        this.productRecommendationHost  = productRecommendationHost;
        this.productServicePort         = productServicePort;
        this.productServiceHost         = productServiceHost;
        this.scheme = scheme;
        this.productServiceIP = scheme + DOUBLE_SLASH + productServiceHost + COLON + this.productServicePort;
        this.recommendationIP = scheme + DOUBLE_SLASH + productRecommendationHost + COLON + this.productRecommendationPort;
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
        String productPath = "/product/";
        String productServiceURL = productServiceIP + productPath + prodID;
        System.out.println("productServiceURL: " + productServiceURL);

        try{
            //ResponseEntity<Product> res = request.getForEntity(productServiceURL,Product.class);
            //System.out.println("res.getBody(): " + res.getBody());
            product = request.getForObject(productServiceURL,Product.class);
        }
        catch(HttpClientErrorException ex){
            System.out.println("HTTP Error getProductService:" + ex.getMessage());
        }
        catch(Exception ex){
            System.out.println("Gen Error getProductService:" + ex.getMessage());
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

    /**
     * Contact with Product Service microservice to store product in MongoDB
     * @param product
     * @return
     */
    public ResponseEntity addProduct(ProductAggregate product){

        /**
         * Compose URI for product-service
         * Example http://localhost:7001/addProduct
         */
        String productPath = "/addProduct";
        String productServiceURL = productServiceIP + productPath;
        System.out.println("productServiceURL: " + productServiceURL);

        Product newProduct = new Product(
                product.getProdID(),
                product.getProdDesc(),
                product.getProdWeight(),
                product.getTrackingID());
        Product savedProd = new Product();

        try{
            savedProd = request.postForObject(
                    productServiceURL,
                    newProduct,
                    Product.class
                    );

            System.out.println("ProductCompositeService savedProd: " + savedProd);
        }
        catch(HttpClientErrorException ex){
            System.out.println("Foutje communicatie met ProductService: " + ex.getMessage());
        }
        catch(Exception ex){
            System.out.println("Alg Foutje communicatie met ProductService: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

        /**
         * Compose URI for recommendation
         * Example http://localhost:7003/addRecommendations
         */
        String recommendationPath = "/addRecommendations";
        String recommendationURL = recommendationIP + recommendationPath;
        System.out.println("recommendationURL: " + recommendationURL);

        List<Recommendation> recommendations = product.getRecommendations();

        try{
            recommendations.stream().map(r->
            request.postForObject(
                    recommendationURL,
                    r,
                    String.class
            )
            ).forEach(rv -> System.out.println("ReturnValue: " + rv));
            /*
            for(Recommendation r: recommendations){

                System.out.println("Recommendation to be saved: " + r);
                Recommendation savedRecommendation = new RestTemplate().postForObject(
                        recommendationURL,
                        r,
                        Recommendation.class
                );
                System.out.println("ProductCompositeService saved Recommendation: " + savedRecommendation);
            }
            */
        }
        catch(HttpClientErrorException ex){
            System.out.println("Foutje communicatie met ProductRecommendation: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
        catch(Exception ex){
            System.out.println("Alg Foutje communicatie met ProductRecommendation: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

        return ResponseEntity.ok().body("Product added");
    }
}
