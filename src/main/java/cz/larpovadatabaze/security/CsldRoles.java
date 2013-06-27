package cz.larpovadatabaze.security;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 26.4.13
 * Time: 8:39
 */
public enum CsldRoles {
    USER("User",1),
    ADMIN("Admin",2),
    EDITOR("Editor",3);

    private String roleName;
    private Short role;
    CsldRoles(String roleName, int role){
        this.roleName = roleName;
        this.role = new Short(String.valueOf(role));
    }

    public String getRoleName(){
        return roleName;
    }

    public Short getRole() {
        return role;
    }
}
