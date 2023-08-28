package dev.manyroads.productrecommendation.service;

import dev.manyroads.api.core.productrecommendation.ProductRecommendation;
import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.productcomposite.TrackingID;
import dev.manyroads.productrecommendation.persistence.RecommendationEntity;
import dev.manyroads.productrecommendation.persistence.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@RestController
public class ProductRecommendationController implements ProductRecommendation {

    RecommendationRepository recommendationRepository;
    RecommendationMapper recommendationMapper;
    Scheduler JDBCScheduler;

    @Autowired
    public ProductRecommendationController(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper, Scheduler jdbcScheduler) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationMapper = recommendationMapper;
        this.JDBCScheduler = jdbcScheduler;
    }

    // ---- Methods ----
    /**
     * Get recommendations per prodID
     * @param prodID
     * @return
     */
    public Flux<Recommendation> getRecommandationByProdID(int prodID){

        System.out.println("Nu in ProductRecommendationController, getRecommandationByProdID " + prodID);

        Flux<Recommendation> flux = Flux.empty();

        // Create a Mono producing its value using the provided Callable.
        flux = Mono.fromCallable(() -> getRecommendations(prodID))
                // Converts Mono into Flux
                .flatMapMany(Flux::fromIterable)
                // Run the blocking code on a thread from the thread pool JDBCScheduler
                .subscribeOn(JDBCScheduler);

        // DB suimulation
        /*
        recommendations.add(new Recommendation(prodID,1,"Beste wat je kan hebben", new TrackingID(prodID).toString()));
        recommendations.add(new Recommendation(prodID,2,"Rommel nooit kopen", new TrackingID(prodID).toString()));
        recommendations.add(new Recommendation(prodID,3,"Moet je snel zijn, op is op", new TrackingID(prodID).toString()));
         */
        // Circuit Breaking Testing
        try{
            //System.out.println("Recommedndation sleeping");
            //sleep(4000);
        }catch(Exception ex){
            System.out.println("Foutje testen sleep: " + ex.getMessage());
        }
        // End Testing
        return flux;
    }

    /**
     * Save a recommendation to mysql db with unique commbination of prodID and recomID
     * @param recommendation
     * @return
     */
    public Mono<Void> addRecommendation(Recommendation recommendation){


            System.out.println("Nu in ProductRecommendationController, addRecommendation " + recommendation);

        // Convert to recommendation entity
        RecommendationEntity entity = recommendationMapper.apiToEntity(recommendation);

        try{
            recommendationRepository.save(entity);

        }catch(Exception ex){
            System.out.println("Foutje in wegschrijven recommendation: " + ex.getMessage());
        }

        return Mono.empty();
    }
    // ---- Submethods ----
    /**
     * Blocking code of DB interaction is placed in this submethod
     * @param prodID
     * @return
     */
    List<Recommendation> getRecommendations(int prodID){

        List<Recommendation> recommendations = new ArrayList<>();
        List<RecommendationEntity> recommendationEntities = new ArrayList<>();

        try{
            recommendationEntities = recommendationRepository.findRecommendationsByProdID(prodID);
        } catch (Exception ex){
            System.out.println("Foutje ophalen recommendations uit DB: " + ex.getMessage());
        }
        // Convert to list of recommendations
        recommendations = recommendationEntities
                .stream()
                .map(e ->
                        recommendationMapper.entityToApi(e)
                )
                .collect(Collectors.toList());
        // Add to list of recommendations the TrackingID for each recommendation
        recommendations.forEach(r -> r.setTrackingID(new TrackingID(prodID).toString()));

        return recommendations;
    }

}
