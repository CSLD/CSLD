package cz.larpovadatabaze.entities;

/**
 * Predefined images
 *
 * User: Michal Kara
 * Date: 26.12.13
 * Time: 23:23
 */
public enum PredefinedImage implements IEntityWithImage.IPredefinedImage {
    DEFAULT_GROUP_ICON(PredefinedImage.class, "img/icon/group_icon.png"),
    DEFAULT_AUTHOR_ICON(PredefinedImage.class, "img/icon/author_icon.png"),
    DEFAULT_GAME_ICON(PredefinedImage.class, "img/icon/question_icon_game.png"),
    PLUS_ICON(PredefinedImage.class, "img/icon/plus_icon.png"),
    SETTINGS_ICON(PredefinedImage.class, "img/icon/settings_icon.png");

    protected final Class baseClass;
    protected final String path;

    public Class getBaseClass() {
        return baseClass;
    }

    public String getPath() {
        return path;
    }

    private PredefinedImage(Class baseClass, String path) {
        this.baseClass = baseClass;
        this.path = path;
    }
}
