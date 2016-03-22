package thedorkknightrises.moviespop;

import java.io.Serializable;

/**
 * Created by samri_000 on 3/19/2016
 */
public class MovieObj implements Serializable {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRating() { return vote_avg; }

    public void setRating(String vote_avg) { this.vote_avg = vote_avg; }

    String title;
    String year;
    String posterUrl;
    String plot;
    String vote_avg;

    public MovieObj(String t, String y, String v, String p, String mPlot) {
        this.title = t;
        this.plot = mPlot;
        this.year = y;
        this.posterUrl = p;
        this.vote_avg = v;
    }
}

