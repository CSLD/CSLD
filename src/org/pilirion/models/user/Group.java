package org.pilirion.models.user;

import org.pilirion.models.game.Author;

import java.util.List;

/**
 * @Version 0.1
 * @Author Jakub Balhar
 * @Since 10.12.12 14:57
 */
public class Group {
    private int id;
    private String name;
    private String image;
    private List<Author> authors;

    public Group(int id, String name, String image, List<Author> authors) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.authors = authors;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public List<Author> getAuthors() {
        return authors;
    }
}
