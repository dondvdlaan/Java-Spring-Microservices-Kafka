package dev.manyroads.productservice.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * ProductEntity for MongoDB
 */
@Document(collection = "products")
public class ProductEntity {

    @Id
    private String id;
    @Version
    private Integer version;
    @Indexed(unique = true)
    private int prodID;
    private String prodDesc;
    private double prodWeight;

    public ProductEntity() {
    }

    public ProductEntity(int prodID, String prodDesc, double prodWeight) {
        this.prodID = prodID;
        this.prodDesc = prodDesc;
        this.prodWeight = prodWeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getProdID() {
        return prodID;
    }

    public void setProdID(int prodID) {
        this.prodID = prodID;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public double getProdWeight() {
        return prodWeight;
    }

    public void setProdWeight(double prodWeight) {
        this.prodWeight = prodWeight;
    }
}
