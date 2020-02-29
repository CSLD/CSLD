package cz.larpovadatabaze.common.models;

public class Page {
    public final int size;
    public final int from;

    public Page(int from, int size) {
        this.size = size;
        this.from = from;
    }
}
