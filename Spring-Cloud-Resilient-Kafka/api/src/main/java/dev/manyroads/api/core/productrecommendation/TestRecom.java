package dev.manyroads.api.core.productrecommendation;

import dev.manyroads.api.productcomposite.TrackingID;

public class TestRecom {

    int prodID;
    int recomID;
    String recomDescription;
    String trackingID;

    public TestRecom() {
    }

    public TestRecom(int prodID, int recomID, String recomDescription, String trackingID) {
        this.prodID = prodID;
        this.recomID = recomID;
        this.recomDescription = recomDescription;
        this.trackingID = trackingID;
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

    public String getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(String trackingID) {
        this.trackingID = trackingID;
    }

    @Override
    public String toString() {
        return "TestRecom{" +
                "prodID=" + prodID +
                ", recomID=" + recomID +
                ", recomDescription='" + recomDescription + '\'' +
                ", trackingID='" + trackingID + '\'' +
                '}';
    }
}
