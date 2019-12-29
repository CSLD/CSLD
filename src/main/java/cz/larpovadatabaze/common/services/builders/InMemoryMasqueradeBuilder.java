package cz.larpovadatabaze.common.services.builders;

import cz.larpovadatabaze.common.entities.*;
import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class InMemoryMasqueradeBuilder implements Builder {
    private Comments comments;
    private CsldUsers users;
    private Games games;
    private CsldGroups groups;
    private Labels labels;
    private Ratings ratings;
    private Upvotes upvotes;

    @Autowired
    public InMemoryMasqueradeBuilder(Comments comments, CsldUsers users, Games games, CsldGroups groups, Labels labels,
                                     Ratings ratings, Upvotes upvotes) {
        this.comments = comments;
        this.users = users;
        this.games = games;
        this.groups = groups;
        this.labels = labels;
        this.ratings = ratings;
        this.upvotes = upvotes;
    }

    @Override
    public void build() {
        String mailTemplate = "%s@masquerade.test";
        CsldUser administrator = new CsldUser(1, String.format(mailTemplate, "administrator"), "Administrator",
                "Administrator", "Prague", "Administrator of Czech Masquerade group", CsldRoles.ADMIN.getRole(), "administrator");
        CsldUser editor = new CsldUser(2, String.format(mailTemplate, "editor"), "Editor",
                "Editor", "Prague", "Editor of Czech Masquerade group", CsldRoles.EDITOR.getRole(), "editor");
        CsldUser user = new CsldUser(3, String.format(mailTemplate, "user"), "User",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");
        users.saveOrUpdate(administrator);
        users.saveOrUpdate(editor);
        users.saveOrUpdate(user);

        Label dramatic = new Label(1, editor, "Dramatic", "Dramatic larp is about drama", true, true);
        Label vampire = new Label(2, editor, "Vampire", "Vampire larp contains vampire in any shape.", true, false);
        List<Label> masqueradeGamesLabels = new ArrayList<>();
        Collections.addAll(masqueradeGamesLabels, dramatic, vampire);
        labels.saveOrUpdate(dramatic);
        labels.saveOrUpdate(vampire);

        groups.saveOrUpdate(new CsldGroup(1, "Nosferatu"));
        groups.saveOrUpdate(new CsldGroup(2, "Toreador"));

        List<CsldUser> authors = new ArrayList<CsldUser>();
        Collections.addAll(authors, editor, administrator);
        Game firstMasquerade = new Game(1, "Masquerade 1", "First try to bring Masquerade into the Czech " +
                "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()));
        Game secondMasquerade = new Game(2, "Masquerade 2", "Second try to bring Masquerade into the Czech " +
                "republic", editor, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()));
        games.saveOrUpdate(firstMasquerade);
        games.saveOrUpdate(secondMasquerade);

        for (int i = 0; i < 40; i++) {
            games.saveOrUpdate(new Game(3 + i, "Masquerades: " + i, "First try to bring Masquerade into the Czech " +
                    "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()), null, 2010 + (i % 5)));
        }

        comments.saveOrUpdate(new Comment(administrator, firstMasquerade, "I liked it"));
        Comment editorComment = new Comment(editor, secondMasquerade, "There were some flwas but overally likeable game.");
        Comment userComment = new Comment(user, secondMasquerade, "My first LARP and it was so freaking awesome.");

        upvotes.saveOrUpdate(new Upvote(editor, userComment));
        upvotes.saveOrUpdate(new Upvote(administrator, userComment));

        upvotes.saveOrUpdate(new Upvote(editor, editorComment));

        ratings.saveOrUpdate(new Rating(user, secondMasquerade, 9));
        ratings.saveOrUpdate(new Rating(editor, firstMasquerade, 6));
        ratings.saveOrUpdate(new Rating(administrator, firstMasquerade, 7));

        ratings.saveOrUpdate(new UserPlayedGame(editor, firstMasquerade));
        ratings.saveOrUpdate(new UserPlayedGame(administrator, firstMasquerade));
        ratings.saveOrUpdate(new UserPlayedGame(user, secondMasquerade));
        ratings.saveOrUpdate(new UserPlayedGame(editor, secondMasquerade));
        ratings.saveOrUpdate(new UserPlayedGame(administrator, secondMasquerade));
    }
}
