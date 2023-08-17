package dev.manyroads.productservice.service;

import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.api.core.productservice.ProductService;
import dev.manyroads.api.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Subscribes to topic and subsequently calls for product service
 */
@Configuration
public class MessageProcessorConfig {

    private ProductService productService;

    @Autowired
    public MessageProcessorConfig(ProductService productService) {
        this.productService = productService;
    }

    @Bean
    Consumer<Event<Integer, Product>> messageProcessor(){

        return e -> {
            System.out.println("In messageProcessor Product ID: " + e.getEventData().getProdID());

            Product product = e.getEventData();
            productService.addProduct(product).block();
        };
    }
}
