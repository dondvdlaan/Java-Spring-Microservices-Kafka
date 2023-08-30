package dev.manyroads.productrecommendation.persistence;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecommendationRepository extends CrudRepository<RecommendationEntity, Integer> {

    List<RecommendationEntity> findByProdID(int prodID);
}
