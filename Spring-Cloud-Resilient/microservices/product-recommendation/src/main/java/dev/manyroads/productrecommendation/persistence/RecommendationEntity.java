package dev.manyroads.productrecommendation.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "recommendations", indexes = { @Index(name = "recom_unique_idx",
                                            unique = true,
                                            columnList = "prodID,recomID") })
public class RecommendationEntity {

    @Id @GeneratedValue
    private int id;

    @Version                // optimistic lock value
    private int version;

    private int prodID;
    private int recomID;
    String recomDescription;

    public RecommendationEntity() {
        this.prodID = -1;
        this.recomID = -1;
        this.recomDescription = "<nothingToSeeHere>";
    }

    public RecommendationEntity(int prodID, int recomID, String recomDescription) {
        this.prodID = prodID;
        this.recomID = recomID;
        this.recomDescription = recomDescription;
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

    public int getRecomID() {
        return recomID;
    }

    public void setRecomID(int recomID) {
        this.recomID = recomID;
    }

    public String getRecomDescription() {
        return recomDescription;
    }

    public void setRecomDescription(String recomDescription) {
        this.recomDescription = recomDescription;
    }

    @Override
    public String toString() {
        return "RecommendationEntity{" +
                "id=" + id +
                ", version=" + version +
                ", prodID=" + prodID +
                ", recomID=" + recomID +
                ", recomDescription='" + recomDescription + '\'' +
                '}';
    }
}
