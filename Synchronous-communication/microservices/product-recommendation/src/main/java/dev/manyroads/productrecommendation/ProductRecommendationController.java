package dev.manyroads.productrecommendation;

import dev.manyroads.api.core.productrecommendation.ProductRecommendation;
import dev.manyroads.api.core.productrecommendation.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductRecommendationController implements ProductRecommendation {

    // ---- Logger ----
    private static final Logger logger = LoggerFactory.getLogger(ProductRecommendationController.class);

    public List<Recommendation> getRecommandationByProdID(int prodID){

        logger.debug("Nu in ProductRecommendationController ");

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(new Recommendation(prodID,"Beste wat je kan hebben"));
        recommendations.add(new Recommendation(prodID,"Rommel nooit kopen"));
        recommendations.add(new Recommendation(prodID,"Moet je snel zijn, op is op"));
        return recommendations;
    }

}
