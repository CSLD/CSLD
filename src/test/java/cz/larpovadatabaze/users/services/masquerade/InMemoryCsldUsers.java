package cz.larpovadatabaze.users.services.masquerade;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InMemoryCsldUsers extends InMemoryCrud<CsldUser, Integer> implements CsldUsers {
    public InMemoryCsldUsers() {
        String mailTemplate = "%s@masquerade.test";
        inMemory.add(new CsldUser(1, String.format(mailTemplate, "administrator"), "Administrator",
                "Administrator", "Prague", "Administrator of Czech Masquerade group", CsldRoles.ADMIN.getRole(), "administrator"));
        inMemory.add(new CsldUser(2, String.format(mailTemplate, "editor"), "Editor",
                "Editor", "Prague", "Editor of Czech Masquerade group", CsldRoles.EDITOR.getRole(), "editor"));
        inMemory.add(new CsldUser(3, String.format(mailTemplate, "user"), "User",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user"));

    }

    @Override
    public void sendForgottenPassword(CsldUser user, EmailAuthentication emailAuthentication, String url) {

    }

    @Override
    public List<CsldUser> getEditors() {
        return inMemory
                .stream()
                .filter(user -> Objects.equals(user.getRole(), CsldRoles.EDITOR.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CsldUser> getAdmins() {
        return inMemory
                .stream()
                .filter(user -> Objects.equals(user.getRole(), CsldRoles.ADMIN.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public CsldUser authenticate(String username, String password) {
        List<CsldUser> candidates = inMemory.stream()
                .filter(user -> user.getPerson().getEmail().equals(username) &&
                        user.getPassword().equals(password))
                .collect(Collectors.toList());
        if (candidates.size() > 0) {
            return candidates.get(0);
        } else {
            return null;
        }
    }

    @Override
    public CsldUser getByEmail(String mail) {
        List<CsldUser> candidates = inMemory.stream()
                .filter(user -> user.getPerson().getEmail().equals(mail))
                .collect(Collectors.toList());
        if (candidates.size() > 0) {
            return candidates.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean saveOrUpdateNewAuthor(CsldUser author) {
        inMemory.add(author);

        return true;
    }

    @Override
    public String getReCaptchaSiteKey() {
        return "fakeKey";
    }

    @Override
    public boolean checkReCaptcha(String response, String remoteIp) throws ReCaptchaTechnicalException {
        return true;
    }

    @Override
    public ResourceReference getIconReference() {
        return new ResourceReference(Images.class, "Image") {
            @Override
            public IResource getResource() {
                return (IResource) attributes -> {
                };
            }
        };
    }
}
