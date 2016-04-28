package thedorkknightrises.moviespop;

import java.io.Serializable;

/**
 * Created by samri_000 on 4/15/2016.
 */
public class ReviewObj implements Serializable {

    String rAuth;
    String rText;

    public ReviewObj(String author, String text) {
        this.rAuth = author;
        this.rText = text;
    }

    public String getRAuth() {
        return rAuth;
    }

    public String getRText() {
        return rText;
    }


    public void setrAuth(String author) {
        this.rAuth = author;
    }

    public void setrText(String text) {
        this.rText = text;
    }

}
