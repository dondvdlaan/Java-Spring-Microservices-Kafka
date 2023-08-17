package dev.manyroads.productservice.service;

import dev.manyroads.api.core.productservice.Product;
import dev.manyroads.productservice.persistence.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "trackingID", ignore = true)
    Product entityToApi(ProductEntity productEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    ProductEntity apiToEntity(Product product);
}
