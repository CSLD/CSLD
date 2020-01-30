package cz.larpovadatabaze.administration.services;

import cz.larpovadatabaze.administration.model.UserRatesOwnGameDto;

import java.util.List;

public interface AdministeredUsers {
    /**
     * The authors that evaluate their own games needs to be clearly scrutinized.
     *
     * @return List of the information about who rated their own games and what games that were.
     */
    List<UserRatesOwnGameDto> getUsersWhoRatesOwnGames();
}
