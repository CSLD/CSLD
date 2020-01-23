package cz.larpovadatabaze.common.services.builders;

import cz.larpovadatabaze.games.services.*;
import cz.larpovadatabaze.users.services.CsldGroups;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.springframework.beans.factory.annotation.Autowired;

public class InMemoryMasqueradeBuilder extends MasqueradeBuilder {
    @Autowired
    public InMemoryMasqueradeBuilder(Comments comments, CsldUsers users, Games games, SimilarGames similarGames, CsldGroups groups, Labels labels,
                                     Ratings ratings, Upvotes upvotes) {
        super(comments, users, games, similarGames, groups, labels, ratings, upvotes);
    }
}
