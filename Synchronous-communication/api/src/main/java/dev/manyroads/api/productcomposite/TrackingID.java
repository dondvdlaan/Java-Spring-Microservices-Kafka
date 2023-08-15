package dev.manyroads.api.productcomposite;

import java.time.LocalDateTime;

public class TrackingID {

    int prodID;
    LocalDateTime localDateTime;

    public TrackingID() {
        this.prodID = -1;
        this.localDateTime = LocalDateTime.now().minusDays(-1);
    }

    public TrackingID(int prodID) {
        this.prodID = prodID;
        this.localDateTime = LocalDateTime.now();
    }

    public int getProdID() {
        return prodID;
    }

    public void setProdID(int prodID) {
        this.prodID = prodID;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "Stamp-" + prodID + "-" + localDateTime;
    }
}
