package dev.manyroads.api.core.productrecommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
}
