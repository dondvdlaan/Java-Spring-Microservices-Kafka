package dev.manyroads.productservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity,String>{

    Mono<ProductEntity> findByProdID(int prodID);
}
