package cz.larpovadatabaze.services.builders;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.security.CsldRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * Created by Balda on 10. 7. 2015.
 */
@Service
public class CzechMasqueradeBuilder {
    @Autowired
    private EntityBuilder persistenceStore;

    public CsldUser administrator;
    public CsldUser editor;
    public CsldUser user;

    public Game firstMasquerade;
    public Game secondMasquerade;

    public Label vampire;
    public Label dramatic;

    public void build() {
        persistenceStore.language(Locale.forLanguageTag("cs"));

        String mailTemplate = "%s@masquerade.test";
        administrator = persistenceStore.user(String.format(mailTemplate, "administrator"), "Administrator",
                "Administrator", "Prague", "Administrator of Czech Masquerade group", CsldRoles.ADMIN.getRole(), "administrator");
        editor = persistenceStore.user(String.format(mailTemplate, "editor"), "Editor",
                "Editor", "Prague", "Editor of Czech Masquerade group", CsldRoles.EDITOR.getRole(), "editor");
        user = persistenceStore.user(String.format(mailTemplate, "user"), "User",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");

        dramatic = persistenceStore.label(editor, "Dramatic", "cs", "Dramatic larp is about drama", true);
        vampire = persistenceStore.label(editor, "Vampire", "cs", "Vampire larp contains vampire in any shape.", true);
        List<Label> masqueradeGamesLabels = new ArrayList<Label>();
        Collections.addAll(masqueradeGamesLabels, dramatic, vampire);

        List<CsldUser> authors = new ArrayList<CsldUser>();
        Collections.addAll(authors, editor, administrator);
        firstMasquerade = persistenceStore.game("Masquerade 1", "First try to bring Masquerade into the Czech " +
                        "republic", "cs", user, authors, masqueradeGamesLabels, Timestamp.from(Instant.now()));
        secondMasquerade = persistenceStore.game("Masquerade 2", "Second try to bring Masquerade into the Czech " +
                "republic", "cs", editor, authors, masqueradeGamesLabels, Timestamp.from(Instant.now()));

        persistenceStore.comment(administrator, firstMasquerade, "I liked it");
        persistenceStore.comment(editor, secondMasquerade, "There were some flwas but overally likeable game.");
        persistenceStore.comment(user, secondMasquerade, "My first LARP and it was so freaking awesome.");

        persistenceStore.rating(user, secondMasquerade, 9);
        persistenceStore.rating(editor, firstMasquerade, 6);
        persistenceStore.rating(administrator, firstMasquerade, 7);

        persistenceStore.playerOfGame(editor, firstMasquerade);
        persistenceStore.playerOfGame(administrator, firstMasquerade);
        persistenceStore.playerOfGame(user, secondMasquerade);
        persistenceStore.playerOfGame(editor, secondMasquerade);
        persistenceStore.playerOfGame(administrator, secondMasquerade);

        persistenceStore.news(editor, "There is going to be third run of the Masquerade.");
        persistenceStore.news(administrator, "Hey guys It was nice running the game, but I am done now. Passing this to editor.");

        persistenceStore.flush();
    }
}