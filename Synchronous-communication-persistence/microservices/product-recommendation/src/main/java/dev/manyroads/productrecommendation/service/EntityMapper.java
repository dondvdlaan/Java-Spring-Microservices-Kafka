package dev.manyroads.productrecommendation.service;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.productrecommendation.persistence.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    RecommendationEntity apiToEntity(Recommendation r);

    Recommendation entityToApi(RecommendationEntity re);
}
