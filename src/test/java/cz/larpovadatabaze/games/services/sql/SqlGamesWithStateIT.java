package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.WithDatabase;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.IGameWithRating;
import cz.larpovadatabaze.common.services.MailService;
import cz.larpovadatabaze.games.services.GamesWithState;
import cz.larpovadatabaze.users.services.AppUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SqlGamesWithStateIT extends WithDatabase {
    private GamesWithState underTest;
    private MailService mockMails;

    @BeforeEach
    public void prepareGamesWithState() {
        mockMails = Mockito.mock(MailService.class);
        underTest = new SqlGamesWithState(
                sessionFactory,
                Mockito.mock(AppUsers.class),
                mockMails
        );
    }

    @Test
    public void returnTheGamesThatUserPlayed() {
        List<IGameWithRating> played = underTest.getPlayedByUser(masqueradeEntities.editor);

        assertThat(played, hasSize(3));
    }

    @Test
    public void returnTheGamesUserWantsToPlay() {
        List<Game> wantsToPlay = underTest.getWantedByUser(masqueradeEntities.user);

        assertThat(wantsToPlay, hasSize(1));
        assertThat(wantsToPlay, hasItem(masqueradeEntities.bestMasquerade));
    }

    @Test
    public void returnTheProperAmountOfThePlayedGames() {
        long amountOfPlayed = underTest.getAmountOfGamesPlayedBy(masqueradeEntities.editor);

        assertThat(amountOfPlayed, is(3L));
    }

    @Test
    public void emailIsSentToThoseWhoWantsToPlayTheGame() {
        Game relatedToTheEvent = session.get(Game.class, masqueradeEntities.bestMasquerade.getId());
        underTest.sendEmailToInterested(relatedToTheEvent, "bestMasquerade/2");

        List<CsldUser> actuallyInterested = new ArrayList<>();
        Collections.addAll(actuallyInterested, masqueradeEntities.user);
        verify(mockMails, times(1))
                .sendInfoAboutNewEventToAllInterested(
                        actuallyInterested,
                        "[LarpDB] Nová událost, u hry: Best Masquerade",
                        "Byla přidána událost, která se váže ke hře, kterou máte nastavenou jako chci hrát. " +
                                "Odkaz: http://larpovadatabaze.cz/bestMasquerade/2");
    }
}
