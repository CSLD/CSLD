package cz.larpovadatabaze.administration.services.sql;

import cz.larpovadatabaze.administration.model.UserRatesOwnGameDto;
import cz.larpovadatabaze.administration.services.AdministeredUsers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SqlAdministeredUsers implements AdministeredUsers {
    @Override
    public List<UserRatesOwnGameDto> getUsersWhoRatesOwnGames() {
        return null;
    }
}
