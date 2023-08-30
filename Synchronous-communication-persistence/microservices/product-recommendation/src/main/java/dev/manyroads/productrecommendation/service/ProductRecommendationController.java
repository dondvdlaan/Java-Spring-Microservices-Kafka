package dev.manyroads.productrecommendation.service;

import dev.manyroads.api.core.productrecommendation.ProductRecommendation;
import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.productrecommendation.persistence.RecommendationEntity;
import dev.manyroads.productrecommendation.persistence.RecommendationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductRecommendationController implements ProductRecommendation {

    RecommendationRepository recommendationRepository;
    EntityMapper entityMapper;

    // With only 1 constructor, no @autowired annotation needed
    public ProductRecommendationController(RecommendationRepository recommendationRepository, EntityMapper entityMapper) {
        this.recommendationRepository = recommendationRepository;
        this.entityMapper = entityMapper;
    }

    public List<Recommendation> getRecommandationByProdID(int prodID){

        System.out.println("Nu in ProductRecommendationController ");

        List<RecommendationEntity> recommendationEntities = new ArrayList<>();
        List<Recommendation> recommendations = new ArrayList<>();
        try{
            recommendationEntities = recommendationRepository.findByProdID(prodID);

        }catch(Exception ex){
            System.out.println("Foutje bij ophalen recomendations: " + ex.getMessage());
        }

        for(RecommendationEntity re : recommendationEntities){
            recommendations.add(entityMapper.entityToApi(re));
        }
        return recommendations;
    }

    @Override
    public ResponseEntity addRecommendations(@RequestBody Recommendation recommendation) {

        System.out.println("Nu in addRecommendations: " + recommendation);

        try{
            // Saving recommendations
                // Mapping from recommendation to entity
                RecommendationEntity re = entityMapper.apiToEntity(recommendation);
                System.out.println("RecommendationEntity: " + re);

                recommendationRepository.save(re);
        } catch(Exception ex){
            System.out.println("Foutje bij wegschrijven recommendation: " + ex.getMessage());
            return ResponseEntity.internalServerError().body("Saving failed");
        }
        return ResponseEntity.ok().body("Recommendation saved");
    }
}
