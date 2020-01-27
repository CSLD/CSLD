package cz.larpovadatabaze.common.services.builders;

import cz.larpovadatabaze.common.entities.*;
import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.users.CsldRoles;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static cz.larpovadatabaze.common.entities.Rating.GameState.PLAYED;
import static cz.larpovadatabaze.common.entities.Rating.GameState.WANT_TO_PLAY;

abstract public class
MasqueradeBuilder implements Builder {
    private Comments comments;
    private CsldUsers users;
    private SimilarGames similarGames;
    private Games games;
    private CsldGroups groups;
    private Labels labels;
    private Ratings ratings;
    private Upvotes upvotes;

    public MasqueradeBuilder(Comments comments, CsldUsers users, Games games, SimilarGames similarGames, CsldGroups groups, Labels labels,
                             Ratings ratings, Upvotes upvotes) {
        this.comments = comments;
        this.users = users;
        this.games = games;
        this.similarGames = similarGames;
        this.groups = groups;
        this.labels = labels;
        this.ratings = ratings;
        this.upvotes = upvotes;
    }

    public MasqueradeEntities build() {
        String mailTemplate = "%s@masquerade.test";
        CsldUser administrator = new CsldUser(String.format(mailTemplate, "administrator"), "Administrator",
                "Administrator", "Prague", "Administrator of Czech Masquerade group", CsldRoles.ADMIN.getRole(), "administrator");
        CsldUser editor = new CsldUser(String.format(mailTemplate, "editor"), "Editor",
                "Editor", "Prague", "Editor of Czech Masquerade group", CsldRoles.EDITOR.getRole(), "editor");
        CsldUser user = new CsldUser(String.format(mailTemplate, "user"), "User",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");
        CsldUser tom = new CsldUser(String.format(mailTemplate, "tom"), "Tom",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");
        CsldUser anna = new CsldUser(String.format(mailTemplate, "anna"), "Anna",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");
        CsldUser joe = new CsldUser(String.format(mailTemplate, "joe"), "Joe",
                "User", "Prague", "User of Czech Masquerade group", CsldRoles.USER.getRole(), "user");
        users.saveOrUpdate(administrator);
        users.saveOrUpdate(editor);
        users.saveOrUpdate(user);
        users.saveOrUpdate(tom);
        users.saveOrUpdate(anna);
        users.saveOrUpdate(joe);

        Label dramatic = new Label(editor, "Dramatic", "Dramatic larp is about drama", true, true);
        Label vampire = new Label(editor, "Vampire", "Vampire larp contains vampire in any shape.", true, false);
        Label emotional = new Label(user, "Emotional", "The games with this label tends to be emotional.", false, false);
        Label chamber = new Label(user, "Chamber", "Larp for small group in a chamber setting. ", false, true);
        List<Label> masqueradeGamesLabels = new ArrayList<>();
        Collections.addAll(masqueradeGamesLabels, dramatic, vampire);
        labels.saveOrUpdate(dramatic);
        labels.saveOrUpdate(vampire);
        labels.saveOrUpdate(emotional);
        labels.saveOrUpdate(chamber);

        CsldGroup nosferatu = new CsldGroup("Nosferatu");
        CsldGroup toreador = new CsldGroup("Toreador");
        groups.saveOrUpdate(nosferatu);
        groups.saveOrUpdate(toreador);

        List<CsldUser> authors = new ArrayList<CsldUser>();
        Collections.addAll(authors, editor, administrator);
        for (int i = 3; i < 43; i++) {
            games.saveOrUpdate(new Game("Masquerades: " + i, i + " th try to bring Masquerade into the Czech " +
                    "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()), null, 2010 + (i % 5)));
        }

        List<CsldUser> authorsIncludingUser = new ArrayList<>();
        Collections.addAll(authorsIncludingUser, user, editor, administrator);
        List<Label> masqueradeLabelsChamber = new ArrayList<>();
        Collections.addAll(masqueradeLabelsChamber, chamber, vampire);
        Game bestMasquerade = new Game("Best Masquerade", "Best Masquerade in the Czech " +
                "republic", user, authorsIncludingUser, masqueradeLabelsChamber, new Timestamp(new Date().getTime()));
        Game wrongMasquerade = new Game("Wrong Masquerade", "Just an error", user, authors,
                masqueradeGamesLabels, new Timestamp(new Date().getTime()));
        wrongMasquerade.setDeleted(true);
        Game firstMasquerade = new Game("Masquerade 1", "First try to bring Masquerade into the Czech " +
                "republic", user, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()));
        Game secondMasquerade = new Game("Masquerade 2", "Second try to bring Masquerade into the Czech " +
                "republic", editor, authors, masqueradeGamesLabels, new Timestamp(new Date().getTime()));
        firstMasquerade.getGroupAuthor().add(nosferatu);
        secondMasquerade.getGroupAuthor().add(nosferatu);

        games.saveOrUpdate(bestMasquerade);
        games.saveOrUpdate(wrongMasquerade);
        games.saveOrUpdate(firstMasquerade);
        games.saveOrUpdate(secondMasquerade);

        similarGames.saveOrUpdate(new SimilarGame(firstMasquerade.getId(), secondMasquerade.getId(), 0.94));
        similarGames.saveOrUpdate(new SimilarGame(firstMasquerade.getId(), bestMasquerade.getId(), 0.72));

        comments.saveOrUpdate(new Comment(administrator, firstMasquerade, "I liked it"));
        Comment editorComment = new Comment(editor, secondMasquerade, "There were some flwas but overally likeable game.");
        Comment userComment = new Comment(user, secondMasquerade, "My first LARP and it was so freaking awesome.");
        comments.saveOrUpdate(editorComment);
        comments.saveOrUpdate(userComment);

        upvotes.saveOrUpdate(new Upvote(editor, userComment));
        upvotes.saveOrUpdate(new Upvote(administrator, userComment));

        upvotes.saveOrUpdate(new Upvote(editor, editorComment));

        Rating userRatedBest = new Rating(user, bestMasquerade, 9, WANT_TO_PLAY);
        ratings.saveOrUpdate(userRatedBest);
        ratings.saveOrUpdate(new Rating(editor, bestMasquerade, 10));
        ratings.saveOrUpdate(new Rating(administrator, bestMasquerade, 9));
        ratings.saveOrUpdate(new Rating(tom, bestMasquerade, 9));
        ratings.saveOrUpdate(new Rating(anna, bestMasquerade, 10));

        Rating userRatedSecond = new Rating(user, secondMasquerade, 9);
        ratings.saveOrUpdate(userRatedSecond);

        ratings.saveOrUpdate(new Rating(editor, firstMasquerade, 6));
        ratings.saveOrUpdate(new Rating(administrator, firstMasquerade, 7));
        ratings.saveOrUpdate(new Rating(tom, firstMasquerade, 6));
        ratings.saveOrUpdate(new Rating(anna, firstMasquerade, 7));
        ratings.saveOrUpdate(new Rating(joe, firstMasquerade, 5));

        ratings.saveOrUpdate(new Rating(editor, secondMasquerade, PLAYED));
        ratings.saveOrUpdate(new Rating(administrator, secondMasquerade, PLAYED));

        return new MasqueradeEntities(
                administrator, editor, user,
                nosferatu, toreador,
                firstMasquerade, secondMasquerade, bestMasquerade, wrongMasquerade,
                vampire, dramatic, emotional, chamber,
                editorComment, userComment,
                userRatedBest, userRatedSecond
        );
    }
}
