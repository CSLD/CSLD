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
public class CzechMasqueradeBuilder{
    @Autowired
    private EntityBuilder persistenceStore;

    private CsldUser administrator;
    private CsldUser editor;
    private CsldUser user;

    private CsldGroup nosferatu;
    private CsldGroup toreador;

    private Game firstMasquerade;
    private Game secondMasquerade;

    private Label vampire;
    private Label dramatic;

    public void build() {
        String mailTemplate = "%s@masquerade.test";
        administrator = persistenceStore.user(String.format(mailTemplate, "administrator"), "Administrator",
                "Administrator", "Prague", "Administrator of Czech Masquerade group", CsldRoles.ADMIN.getRole(), "administrator");
        editor = persistenceStore.user(String.format(mailTemplate, "editor"), "Editor",
                "Editor", "Prague", "Editor of Czech Masquerade group", CsldRoles.EDITOR.getRole(), "editor");
        user = persistenceStore.user(String.format(mailTemplate, "user"), "User",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");

        dramatic = persistenceStore.label(editor, "Dramatic", "Dramatic larp is about drama", true, true);
        vampire = persistenceStore.label(editor, "Vampire", "Vampire larp contains vampire in any shape.", true, false);
        List<Label> masqueradeGamesLabels = new ArrayList<Label>();
        Collections.addAll(masqueradeGamesLabels, dramatic, vampire);

        nosferatu = persistenceStore.group("Nosferatu");
        toreador = persistenceStore.group("Toreador");

        List<CsldUser> authors = new ArrayList<CsldUser>();
        Collections.addAll(authors, editor, administrator);
        firstMasquerade = persistenceStore.game("Masquerade 1", "First try to bring Masquerade into the Czech " +
                        "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()));
        secondMasquerade = persistenceStore.game("Masquerade 2", "Second try to bring Masquerade into the Czech " +
                "republic", editor, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()));

        for(int i = 0; i < 40; i++) {
            persistenceStore.game("Masquerades: " + i , "First try to bring Masquerade into the Czech " +
                    "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()), null, 2010 + (i % 5));
        }
        persistenceStore.similarGame(0,1,0.9);
        persistenceStore.similarGame(0,2,0.5);

        persistenceStore.comment(administrator, firstMasquerade, "I liked it");
        Comment editorComment = persistenceStore.comment(editor, secondMasquerade, "There were some flwas but overally likeable game.");
        Comment userComment = persistenceStore.comment(user, secondMasquerade, "My first LARP and it was so freaking awesome.");

        persistenceStore.plusOne(editor, userComment);
        persistenceStore.plusOne(administrator, userComment);

        persistenceStore.plusOne(editor, editorComment);

        persistenceStore.rating(user, secondMasquerade, 9);
        persistenceStore.rating(editor, firstMasquerade, 6);
        persistenceStore.rating(administrator, firstMasquerade, 7);

        persistenceStore.playerOfGame(editor, firstMasquerade);
        persistenceStore.playerOfGame(administrator, firstMasquerade);
        persistenceStore.playerOfGame(user, secondMasquerade);
        persistenceStore.playerOfGame(editor, secondMasquerade);
        persistenceStore.playerOfGame(administrator, secondMasquerade);

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
}