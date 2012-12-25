package org.pilirion.models.user;

import java.sql.Connection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 26.6.12
 * Time: 15:20
 */
public class Role_Has_Resources {
    private Connection db;

    public Role_Has_Resources(Connection db){
        this.db = db;
    }

    public boolean hasRoleResource(Role role, String baseUrl){
        Resources resources = new Resources(db);
        List<Resource> resourceList = resources.getResourceForRole(role.getId());
        boolean hasRole = false;
        for(Resource res: resourceList){
            if(res.getPath().equals(baseUrl)) {
                hasRole = true;
            }
        }
        return hasRole;
    }
}
