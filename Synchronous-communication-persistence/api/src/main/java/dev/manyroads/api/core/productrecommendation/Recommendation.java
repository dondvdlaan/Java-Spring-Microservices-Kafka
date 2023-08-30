package dev.manyroads.api.core.productrecommendation;

public class Recommendation{
    private int prodID;
    private int recommID;
    private String recommendation;

    public Recommendation() {
    }

    public Recommendation(int prodID, int recommID, String recommendation) {
        this.prodID = prodID;
        this.recommID = recommID;
        this.recommendation = recommendation;
    }

    public int getProdID() {
        return prodID;
    }

    public void setProdID(int prodID) {
        this.prodID = prodID;
    }

    public int getRecommID() {
        return recommID;
    }

    public void setRecommID(int recommID) {
        this.recommID = recommID;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "prodID=" + prodID +
                ", recommID=" + recommID +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
