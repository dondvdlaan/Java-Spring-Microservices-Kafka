package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.event.Event;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

import static dev.manyroads.api.event.Event.Type.CREATE;

@Service
public class ProductCompositeService {

    // ---- Constants ----
    final String DOUBLE_SLASH = "://";
    final String COLON = ":";

    // ---- Fields ----
    String productServicePort;
    String productServiceHost;
    String productRecommendationPort;
    String productRecommendationHost;
    String scheme;
    String productServiceIP;
    RestTemplate request;
    private final StreamBridge streamBridge;
    Scheduler publishEventScheduler;
    WebClient webClient;

    // ---- Constructor ----
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
            RestTemplate request,
            StreamBridge streamBridge,
            Scheduler publishEventScheduler,
            /* Takes Type WebClient.Builder with LoadBalancer included from Bean loadBalancedWebClientBuilder()
            in ProductCompositeApplication
             */
            WebClient.Builder webClientBuilder) {

        this.productRecommendationPort  = productRecommendationPort;
        this.productRecommendationHost  = productRecommendationHost;
        this.productServicePort         = productServicePort;
        this.productServiceHost         = productServiceHost;
        this.scheme = scheme;
        this.productServiceIP = scheme + DOUBLE_SLASH + productServiceHost;
        //this.productServiceIP = scheme + DOUBLE_SLASH + productServiceHost + COLON + this.productServicePort;
        this.request = request;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
        // All concurrent requests can now be made savely by the webclient(inmutable)
        this.webClient = webClientBuilder.build();
    }

    // --- Methods ----

    /**
     * Method te get ProductAggregate from databases MongoDB and Mysql. The method calls 2 different microservices
     * @param prodID
     * @return
     */
    @Retry(name = "cbProduct")
    @TimeLimiter(name = "cbProduct")
    @CircuitBreaker(name = "cbProduct", fallbackMethod = "getFallbackProduct")
    public Mono<ProductAggregate> getProdByID(int prodID){

        System.out.println("Now in \"protected Mono<ProductAggregate> getProdByID(int prodID)\" ");
        /*
         Get Product from ProductService by prodID
         */
        Mono<Product> monoProduct = Mono.empty();

        /*
         Compose URI
         Example http://localhost:7001/product/123
         */
        String productPath = "/product/";
        String productServiceURL = productServiceIP + productPath + prodID;
        System.out.println("productServiceURL: " + productServiceURL);

        /*
         Get recommendations from ProductRecommendation by prodID
         */
        Mono<List<Recommendation>> monoRecommendations = Mono.empty();
        List<Recommendation> recommendations = new ArrayList<>();

        // Compose URI
        String productRecommendationIP = scheme + DOUBLE_SLASH + productRecommendationHost;
        //String productRecommendationIP = scheme + DOUBLE_SLASH + productRecommendationHost + COLON + productRecommendationPort;
        String recommendationPath = "/recommendation";
        String productQuery = "?prodID=";
        String productRecommendationURL =
                productRecommendationIP +
                        recommendationPath +
                        productQuery +
                        prodID;
        System.out.println("productRecommendationURL: " + productRecommendationURL);

        /*
         Prepare WebClient and return type ProductAggregate
         */
        //WebClient webClient1 = webClient.get().uri(productServiceURL);
        //WebClient webClient2 = WebClient.create(productRecommendationURL);
        Mono<ProductAggregate> monoProductAggregate = Mono.empty();

        try{

            monoProduct = webClient
                    .get()
                    .uri(productServiceURL)
                    .retrieve()
                    .bodyToMono(Product.class);

            monoRecommendations = webClient
                    .get()
                    .uri(productRecommendationURL)
                    .retrieve()
                    .bodyToFlux(Recommendation.class)
                    .collectList();

            monoRecommendations.subscribe(System.out::println);

            monoProductAggregate = Mono.zip(monoProduct, monoRecommendations, (p, r)->{

                        ProductAggregate productAggregate = new ProductAggregate();
                        productAggregate.setProdID(p.getProdID());
                        productAggregate.setProdDesc(p.getProdDesc());
                        productAggregate.setProdWeight(p.getProdWeight());
                        productAggregate.setTrackingID(p.getTrackingID());
                        productAggregate.setRecommendations(r);
                        return productAggregate;
                    });
        }
        catch(HttpClientErrorException ex){
            System.out.println("HTTP Error getProductService:" + ex.getMessage());
        }
        catch(Exception ex){
            System.out.println("Gen Error getProductService:" + ex.getMessage());
        }

        return monoProductAggregate;
    }

    /*
    Fallback method is invoked when CallNotPermittedException ex is thrown.
    CallNotPermittedException ex is thrown when CB is OPEN and new calls coming in for ProductAggregate
    Fallback method returns  fallback ProductAggregate
     */
    private Mono<ProductAggregate> getFallbackProduct(int prodID, CallNotPermittedException ex){

        System.out.println("Now in fall back method Producr, message: " + ex.getMessage());
        List<Recommendation> rs = List.of(new Recommendation(
                prodID,
                11,
                "No more recommendations",
                "noTrackingID"));
        ProductAggregate pa = new ProductAggregate(
                prodID,
                "fallback Product",
                20D,
                "noTrackinID",
                rs);
        return Mono.just(pa);
    }

    /**
     * Aggregate Product by synchroneously(RestTemplate) retrieving ProductService and ProductRecommndation
     * by prodID
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
     * Asynchronuos Message-Event streaming is used
     * @param product
     * @return
     */
     public Mono<Product> addProduct(Product product){

        return Mono.fromCallable(() -> {

            sendMessage("products-out-0",
                    new Event(CREATE, product.getProdID(), product));

            return product;
            })
            /*
            Returning streamBridge(blocking code) is executed on a thread provided by
            publishEventScheduler bean
            */
            .subscribeOn(publishEventScheduler);
    }

    /**
     * Contact with Recommendation Service microservice to store recommendation in MySQL
     * @param recommendation
     * @return
     */
    public Mono<Recommendation> addRecommendation(Recommendation recommendation){

        return Mono.fromCallable(() -> {

                    sendMessage("recommendations-out-0",
                            new Event(CREATE, recommendation.getProdID(), recommendation));

                    return recommendation;
                })
                // Returning streamBridge(blocking code) is executed on a thread provided by publishEventScheduler
                .subscribeOn(publishEventScheduler);
    }

    // --- Sub-methods ---

    /**
     * Build message and send by way of streamBridge to the messaging system, which will
     * publish it on the topic defined in the properties file
     * @param bindingName
     * @param event
     */
    private void sendMessage(String bindingName, Event event) {

        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getEventKey())
                .build();
        System.out.println("** In sendMessage ** : " + message);
        streamBridge.send(bindingName, message);
    }
}
