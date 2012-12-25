package org.pilirion.url;

/**
 * It represents one Variable in the URL.
 */
public class Variable {
    private String name;
    private int position;

    /**
     * @param name Name of the variable
     * @param position position of the variable in the url.
     */
    public Variable(String name, int position){
        this.position = position;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
