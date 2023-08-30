package dev.manyroads.productrecommendation.persistence;

import jakarta.persistence.*;

/*
Create Table with unique value for combined key "prod-recomm-id"
 */
@Entity
@Table(indexes = {@Index(name = "prod-recomm-id", columnList = "prodID, recommID", unique = true)})
public class RecommendationEntity {

    @Id @GeneratedValue
    private int id;
    @Version
    private int version;
    private int prodID;
    private int recommID;
    private String recommendation;

    public RecommendationEntity() {
    }

    public RecommendationEntity(int prodID, int recommID, String recommendation) {
        this.prodID = prodID;
        this.recommID = recommID;
        this.recommendation = recommendation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
        return "RecommendationEntity{" +
                "id=" + id +
                ", version=" + version +
                ", prodID=" + prodID +
                ", recommID=" + recommID +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}
