package dev.manyroads.api.core.productrecommendation;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductRecommendation {

    /**
     * Example URL http://localhost:7003/recommendation?prodID=123
     * @param prodID
     * @return
     */
    @GetMapping (
            value = ("/recommendation"),
            produces = "application/json")
    public Flux<Recommendation> getRecommandationByProdID(@RequestParam( value = "prodID") int prodID);

    @PostMapping (
            value = ("recommendation/addRecommendation"),
            produces = "application/json")
    public Mono<Void> addRecommendation(@RequestBody Recommendation recommendation);

}
