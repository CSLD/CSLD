package cz.larpovadatabaze.games.models;

public class RestGameDto {
    public final String name;
    public final String description;
    public final Integer year;
    public final String web;
    public final Double averageRating;
    public final Double totalRating;

    public RestGameDto(String name, String description, Integer year, String web, Double averageRating, Double totalRating) {
        this.name = name;
        this.description = description;
        this.year = year;
        this.web = web;
        this.averageRating = averageRating;
        this.totalRating = totalRating;
    }
}
