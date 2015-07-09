package cz.larpovadatabaze.entities;

import java.io.Serializable;

/**
 * Created by jbalhar on 7/9/2015.
 */
public class News {
    private CsldUser author;
    private String text;

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
}
