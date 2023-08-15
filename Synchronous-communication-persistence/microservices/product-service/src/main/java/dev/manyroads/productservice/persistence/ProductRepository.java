package dev.manyroads.productservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


public interface ProductRepository extends
                                    CrudRepository<ProductEntity,String>,
                                    PagingAndSortingRepository<ProductEntity,String> {


    Optional<ProductEntity> findByProdID(int prodID);
}
