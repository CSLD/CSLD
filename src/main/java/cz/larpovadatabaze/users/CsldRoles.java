package cz.larpovadatabaze.users;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 26.4.13
 * Time: 8:39
 */
public enum CsldRoles {
    USER("User", 1),
    EDITOR("Editor", 2),
    ADMIN("Admin", 3),
    AUTHOR("Author", 4),
    ANONYMOUS("Anonymous", 0);

    private String roleName;
    private Short role;

    CsldRoles(String roleName, int role) {
        this.roleName = roleName;
        this.role = Short.valueOf(String.valueOf(role));
    }

    public String getRoleName() {
        return roleName;
    }

    public Short getRole() {
        return role;
    }

    public static Short getRoleByName(String name) {
        if (name.equals("Administrátor")) {
            return (short) 3;
        } else if (name.equals("Editor")) {
            return (short) 2;
        } else {
            return (short) 1;
        }
    }

    public static String getNameByRole(Short role) {
        if (role == 3) {
            return "Administrátor";
        } else if (role == 2) {
            return "Editor";
        } else {
            return "Uživatel";
        }
    }
}
