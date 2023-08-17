package dev.manyroads.productrecommendation.persistence;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

public interface RecommendationRepository extends CrudRepository<RecommendationEntity,Integer> {

    List<RecommendationEntity> findRecommendationsByProdID(int prodID);
}
