package dev.manyroads.productrecommendation.service;

import dev.manyroads.api.core.productrecommendation.ProductRecommendation;
import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.core.productservice.ProductService;
import dev.manyroads.api.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Subscribes to topic recommendations(see properties) and subsequently calls for recommendation service
 */
@Configuration
public class MessageProcessorConfig {

    ProductRecommendation productRecommendation;

    @Autowired
    public MessageProcessorConfig(ProductRecommendation productRecommendation) {
        this.productRecommendation = productRecommendation;
    }

    @Bean
    Consumer<Event<Integer, Recommendation>> messageProcessor(){

        return e -> {
            System.out.println("In messageProcessor Recommendation ID: " + e.getEventData().getRecomID());

            Recommendation recommendation = e.getEventData();
            productRecommendation.addRecommendation(recommendation).block();
        };
    }
}
