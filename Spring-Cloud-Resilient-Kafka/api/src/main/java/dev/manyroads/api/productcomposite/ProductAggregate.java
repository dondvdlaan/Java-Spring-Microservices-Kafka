package dev.manyroads.api.productcomposite;

import dev.manyroads.api.core.productrecommendation.Recommendation;
import dev.manyroads.api.core.productservice.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAggregate extends Product {

    List<Recommendation> recommendations;

    // ---- Constructors ---
    public ProductAggregate() {
        super();
        this.recommendations = new ArrayList<Recommendation>();
    }

    public ProductAggregate(int prodID, String prodDesc, double prodWeight, String trackingID, List<Recommendation> recommendations) {
        super(prodID, prodDesc, prodWeight, trackingID);
        this.recommendations = recommendations;
    }

    //---- Methods ----
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public String toString() {
        return super.toString() + " recommendations = " + recommendations ;
    }
}
