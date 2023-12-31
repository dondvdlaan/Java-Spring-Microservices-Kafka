package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.event.Event;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
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
import static reactor.core.publisher.Flux.empty;

@Component
public class ProductCompositeService {

    // ---- Logger ----
    final static Logger logger = LoggerFactory.getLogger(ProductCompositeService.class);

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
        /*
        To communicate with the Eureka Discovery, a base URL with a virtual hostname is composed.
        This virtual hostname is previously used by the microservice to register itself at Eureka
         */
        this.productServiceIP = scheme + DOUBLE_SLASH + productServiceHost;
        this.request = request;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
        // All concurrent requests can now be made savely by the webclient(inmutable)
        this.webClient = webClientBuilder.build();
    }

    // --- Methods ----
    protected Mono<ProductAggregate> getProdByID(int prodID){

        /**
         * Get Product from ProductService by prodID
         */
        Mono<Product> monoProduct = Mono.empty();

        /**
         * Compose URI
         * Example virtual Eureka host: http://product/product/40
         */
        String productPath = "/product/";
        String productServiceURL = productServiceIP + productPath + prodID;
        System.out.println("productServiceURL: " + productServiceURL);

        /**
         * Get recommendations from ProductRecommendation by prodID
         */
        //Flux<Recommendation> fluxRecommendations = Flux.empty();
        List<Recommendation> recommendations = new ArrayList<>();

        /*
        Compose URI
        Example virtual Eureka host: http://recommendation/recommendation?prodID=40
         */
        String productRecommendationIP = scheme + DOUBLE_SLASH + productRecommendationHost;
        String recommendationPath = "/recommendation";
        String productQuery = "?prodID=";
        String productRecommendationURL =
                productRecommendationIP +
                        recommendationPath +
                        productQuery +
                        prodID;
        System.out.println("productRecommendationURL: " + productRecommendationURL);

        /**
         * Prepare WebClient and return type ProductAggregate
         */
        Mono<ProductAggregate> monoProductAggregate = Mono.empty();

        try{

            monoProduct = webClient
                    .get()
                    .uri(productServiceURL)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .doOnError(err -> logger.error("Failure retrieving product:{}", err.getMessage()))
                    ;

            Flux<Recommendation> fluxRecommendations = webClient
                    .get()
                    .uri(productRecommendationURL)
                    .retrieve()
                    .bodyToFlux(Recommendation.class)
                    // Return empty result in case of failure to retrieve recommendations
                    .onErrorResume(err -> empty() )
                    ;

            fluxRecommendations.subscribe(System.out::println);

            monoProductAggregate = Mono.zip(monoProduct, fluxRecommendations.collectList(), (p, r)->{

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

    /**
     * Contact with Product Service microservice to store product in MongoDB
     * Asynchronuos Message-Event sctreaming is used
     * @param product
     * @return
     */
    public Mono<Product> addProduct(Product product){

        return Mono.fromCallable(() -> {

            sendMessage("products-out-0",
                    new Event(CREATE, product.getProdID(), product));

            return product;
        })
        // Returning streamBridge(blocking code) is executed on a thread provided by publishEventScheduler
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
     * publish it on the topic deifined in the properties file
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
