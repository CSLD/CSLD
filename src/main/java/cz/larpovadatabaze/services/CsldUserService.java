package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface CsldUserService extends GenericService<CsldUser> {
    public CsldUser getById(Integer id);

    boolean saveOrUpdate(CsldUser user);

    void flush();

    public List<CsldUser> getAuthorsByGames();

    List<CsldUser> getEditors();

    List<CsldUser> getAdmins();

    CsldUser getWithMostComments();

    CsldUser getWithMostAuthored();

    CsldUser authenticate(String username, String password);

    List<CsldUser> getAuthorsByBestGame();

    List<CsldUser> getOrderedByName();

    List<CsldUser> getOrderedByComments();

    List<CsldUser> getOrderedByPlayed();

    List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException;

    List<CsldUser> getOrderedUsersByName();

    List<CsldUser> getOrderedUsersByComments();

    List<CsldUser> getOrderedUsersByPlayed();

    CsldUser getByEmail(String mail);
}
