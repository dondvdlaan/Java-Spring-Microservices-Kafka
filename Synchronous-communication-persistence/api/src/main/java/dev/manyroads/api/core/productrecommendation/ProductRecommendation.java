package dev.manyroads.api.core.productrecommendation;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<Recommendation> getRecommandationByProdID(@RequestParam( value = "prodID") int prodID);

    @PostMapping(
            value = ("/addRecommendations"),
            produces = "application/json")
    public ResponseEntity addRecommendations(@RequestBody Recommendation recommendation);
}
