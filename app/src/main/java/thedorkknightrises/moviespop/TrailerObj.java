package thedorkknightrises.moviespop;

import java.io.Serializable;

/**
 * Created by samri_000 on 4/2/2016.
 */
public class TrailerObj implements Serializable {

    String trName;
    String trId;

    public TrailerObj(String name, String source) {
        this.trName = name;
        this.trId = source;
    }

    public String getTrName() {
        return trName;
    }

    public String getTrId() {
        return trName;
    }

    public void setTrName(String name) {
        this.trName = name;
    }

    public void setTrId(String id) {
        this.trId = id;
    }
}
