package dev.manyroads.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.event.Event;
import dev.manyroads.api.productcomposite.ProductAggregate;
import dev.manyroads.api.productcomposite.ProductComposite;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

import static dev.manyroads.api.event.Event.Type.CREATE;

@Component
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
            Scheduler publishEventScheduler) {

        this.productRecommendationPort  = productRecommendationPort;
        this.productRecommendationHost  = productRecommendationHost;
        this.productServicePort         = productServicePort;
        this.productServiceHost         = productServiceHost;
        this.scheme = scheme;
        this.productServiceIP = scheme + DOUBLE_SLASH + productServiceHost + COLON + this.productServicePort;
        this.request = request;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    // --- Methods ----
    protected Mono<ProductAggregate> getProdByID(int prodID){

        /**
         * Get Product from ProductService by prodID
         */
        Mono<Product> monoProduct = Mono.empty();

        /**
         * Compose URI
         * Example http://localhost:7001/product/123
         */
        String productPath = "/product/";
        String productServiceURL = productServiceIP + productPath + prodID;
        System.out.println("productServiceURL: " + productServiceURL);

        /**
         * Get recommendations from ProductRecommendation by prodID
         */
        Mono<List<Recommendation>> monoRecommendations = Mono.empty();
        List<Recommendation> recommendations = new ArrayList<>();

        /*
        Compose URI
        Example: http://recommendation:8080/recommendation?prodID=40
         */
        String productRecommendationIP = scheme + DOUBLE_SLASH + productRecommendationHost + COLON + productRecommendationPort;
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
        WebClient prodClient = WebClient.create(productServiceURL);
        WebClient recommClient = WebClient.create(productRecommendationURL);
        Mono<ProductAggregate> monoProductAggregate = Mono.empty();

            monoProduct = prodClient
                    .get()
                    .retrieve()
                    .bodyToMono(Product.class)
                    .onErrorMap(WebClientResponseException.class, Throwable::getCause);

            monoRecommendations = recommClient
                    .get()
                    .retrieve()
                    .bodyToFlux(Recommendation.class)
                    .collectList()
                    // Case of error, programm continues and returns empty recommendations
                    .onErrorResume(err -> Mono.empty());

            // Debug
            monoRecommendations.subscribe(System.out::println);

            // Wait for both Product and Recommendations to arrive
            monoProductAggregate = Mono.zip(monoProduct, monoRecommendations, (p, r)->{

                        ProductAggregate productAggregate = new ProductAggregate();
                        productAggregate.setProdID(p.getProdID());
                        productAggregate.setProdDesc(p.getProdDesc());
                        productAggregate.setProdWeight(p.getProdWeight());
                        productAggregate.setTrackingID(p.getTrackingID());
                        productAggregate.setRecommendations(r);
                        return productAggregate;
                    });

        return monoProductAggregate;
    }

    /**
     * Contact with Product Service microservice to store product in MongoDB
     * @param product
     * @return
     */
    public Mono<Product> addProduct(Product product){

        System.out.println("ProductCompositeService product: " + product);

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

        System.out.println("ProductCompositeService recommendation: " + recommendation);

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
