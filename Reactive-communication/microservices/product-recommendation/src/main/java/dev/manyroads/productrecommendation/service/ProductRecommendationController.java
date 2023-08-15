package dev.manyroads.productrecommendation.service;

import dev.manyroads.api.core.productrecommendation.ProductRecommendation;
import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.productcomposite.TrackingID;
import dev.manyroads.productrecommendation.persistence.RecommendationEntity;
import dev.manyroads.productrecommendation.persistence.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductRecommendationController implements ProductRecommendation {

    RecommendationRepository recommendationRepository;
    RecommendationMapper recommendationMapper;

    @Autowired
    public ProductRecommendationController(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
    }

    /**
     * Get recommendations per prodID
     * @param prodID
     * @return
     */
    public Mono<List<Recommendation>> getRecommandationByProdID(int prodID){

        System.out.println("Nu in ProductRecommendationController, getRecommandationByProdID " + prodID);

        List<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(new Recommendation(prodID,1,"Beste wat je kan hebben", new TrackingID(prodID).toString()));
        recommendations.add(new Recommendation(prodID,2,"Rommel nooit kopen", new TrackingID(prodID).toString()));
        recommendations.add(new Recommendation(prodID,3,"Moet je snel zijn, op is op", new TrackingID(prodID).toString()));

        return Mono.just(recommendations);
    }

    /**
     * Save a recommendation to db with unique commbination of prodID and recomID
     * @param recommendation
     * @return
     */
    public Mono<Void> addRecommendation(Recommendation recommendation){

        RecommendationEntity entity = recommendationMapper.apiToEntity(recommendation);

        try{
            recommendationRepository.save(entity);

        }catch(Exception ex){
            System.out.println("Foutje in wegschrijven recommendetion: " + ex.getMessage());
        }

        return Mono.empty();
    }

}
