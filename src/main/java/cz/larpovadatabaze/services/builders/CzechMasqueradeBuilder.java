package cz.larpovadatabaze.services.builders;

import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.security.CsldRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CzechMasqueradeBuilder {
    private final EntityStoreBuilder persistenceStore;

    private CsldUser administrator;
    private CsldUser editor;
    private CsldUser user;

    private CsldGroup nosferatu;
    private CsldGroup toreador;

    private Game firstMasquerade;
    private Game secondMasquerade;

    private Label vampire;
    private Label dramatic;

    private Comment editorComment;
    private Comment userComment;

    @Autowired
    public CzechMasqueradeBuilder(EntityStoreBuilder persistenceStore) {
        this.persistenceStore = persistenceStore;
    }

    public void build() {
        String mailTemplate = "%s@masquerade.test";
        administrator = persistenceStore.user(new CsldUser(String.format(mailTemplate, "administrator"), "Administrator",
                "Administrator", "Prague", "Administrator of Czech Masquerade group", CsldRoles.ADMIN.getRole(), "administrator"));
        editor = persistenceStore.user(new CsldUser(String.format(mailTemplate, "editor"), "Editor",
                "Editor", "Prague", "Editor of Czech Masquerade group", CsldRoles.EDITOR.getRole(), "editor"));
        user = persistenceStore.user(new CsldUser(String.format(mailTemplate, "user"), "User",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user"));

        dramatic = persistenceStore.label(new Label(editor, "Dramatic", "Dramatic larp is about drama", true, true));
        vampire = persistenceStore.label(new Label(editor, "Vampire", "Vampire larp contains vampire in any shape.", true, false));
        List<Label> masqueradeGamesLabels = new ArrayList<Label>();
        Collections.addAll(masqueradeGamesLabels, dramatic, vampire);

        nosferatu = persistenceStore.group(new CsldGroup("Nosferatu"));
        toreador = persistenceStore.group(new CsldGroup("Toreador"));

        List<CsldUser> authors = new ArrayList<CsldUser>();
        Collections.addAll(authors, editor, administrator);
        firstMasquerade = persistenceStore.game(new Game("Masquerade 1", "First try to bring Masquerade into the Czech " +
                "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime())));
        secondMasquerade = persistenceStore.game(new Game("Masquerade 2", "Second try to bring Masquerade into the Czech " +
                "republic", editor, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime())));

        for (int i = 0; i < 40; i++) {
            persistenceStore.game(new Game("Masquerades: " + i, "First try to bring Masquerade into the Czech " +
                    "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()), null, 2010 + (i % 5)));
        }
        persistenceStore.similarGame(new SimilarGame(firstMasquerade.getId(), secondMasquerade.getId(), 0.9));

        persistenceStore.comment(new Comment(administrator, firstMasquerade, "I liked it"));
        editorComment = persistenceStore.comment(new Comment(editor, secondMasquerade, "There were some flwas but overally likeable game."));
        userComment = persistenceStore.comment(new Comment(user, secondMasquerade, "My first LARP and it was so freaking awesome."));

        persistenceStore.plusOne(new Upvote(editor, userComment));
        persistenceStore.plusOne(new Upvote(administrator, userComment));

        persistenceStore.plusOne(new Upvote(editor, editorComment));

        persistenceStore.rating(new Rating(user, secondMasquerade, 9));
        persistenceStore.rating(new Rating(editor, firstMasquerade, 6));
        persistenceStore.rating(new Rating(administrator, firstMasquerade, 7));

        persistenceStore.playerOfGame(new UserPlayedGame(editor, firstMasquerade));
        persistenceStore.playerOfGame(new UserPlayedGame(administrator, firstMasquerade));
        persistenceStore.playerOfGame(new UserPlayedGame(user, secondMasquerade));
        persistenceStore.playerOfGame(new UserPlayedGame(editor, secondMasquerade));
        persistenceStore.playerOfGame(new UserPlayedGame(administrator, secondMasquerade));

        persistenceStore.flush();
    }

    public CsldUser getAdministrator() {
        return administrator;
    }

    public CsldUser getEditor() {
        return editor;
    }

    public CsldUser getUser() {
        return user;
    }

    public Game getFirstMasquerade() {
        return firstMasquerade;
    }

    public Game getSecondMasquerade() {
        return secondMasquerade;
    }

    public Label getVampire() {
        return vampire;
    }

    public Label getDramatic() {
        return dramatic;
    }

    public CsldGroup getNosferatu() {
        return nosferatu;
    }

    public CsldGroup getToreador() {
        return toreador;
    }

    public Comment getEditorComment() {
        return editorComment;
    }

    public Comment getUserComment() {
        return userComment;
    }
}