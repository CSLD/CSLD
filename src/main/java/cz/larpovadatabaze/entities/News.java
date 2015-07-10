package cz.larpovadatabaze.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by jbalhar on 7/9/2015.
 */
public class News implements Serializable {
    private CsldUser author;
    private String text;
    private Timestamp added;

    public CsldUser getAuthor() {
        return author;
    }

    public void setAuthor(CsldUser author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }
}
