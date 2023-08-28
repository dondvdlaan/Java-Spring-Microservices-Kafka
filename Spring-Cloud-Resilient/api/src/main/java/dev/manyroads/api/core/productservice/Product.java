package dev.manyroads.api.core.productservice;

import dev.manyroads.api.productcomposite.TrackingID;

public class Product {

    int prodID;
    String prodDesc;
    double prodWeight;
    String trackingID;

    public Product() {
        this.prodID = -1;
        this.prodDesc = "<nothingToSeeHere";
        this.prodWeight = -1D;
        this.trackingID = new TrackingID().toString();
    }

    public Product(int prodID, String prodDesc, double prodWeight, String trackingID) {
        this.prodID = prodID;
        this.prodDesc = prodDesc;
        this.prodWeight = prodWeight;
        this.trackingID = trackingID;
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

    public String getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(String trackingID) {
        this.trackingID = trackingID;
    }

    @Override
    public String toString() {
        return "Product: " +
                "prodID =" + prodID +
                ", prodDesc ='" + prodDesc + '\'' +
                ", prodWeight =" + prodWeight +
                ", trackingID =" + trackingID +
                '}';
    }
}
