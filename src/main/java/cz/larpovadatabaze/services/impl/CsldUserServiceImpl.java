package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.CsldUserDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.CsldUserService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class CsldUserServiceImpl implements CsldUserService {
    @Autowired
    private CsldUserDAO csldUserDao;

    @Override
    public CsldUser getById(Integer id) {
        return csldUserDao.findById(id, false);
    }

    @Override
    public void saveOrUpdate(CsldUser user) {
        csldUserDao.saveOrUpdate(user);
    }

    @Override
    public void flush() {
        csldUserDao.flush();
    }

    public List<CsldUser> getAuthorsByGames(){
        return csldUserDao.getAuthorsByGames();
    }

    @Override
    public List<CsldUser> getAll() {
        return csldUserDao.findAll();
    }

    @Override
    public List<CsldUser> getUnique(CsldUser example) {
        return csldUserDao.findByExample(example, new String[]{});
    }

    @Override
    public void remove(CsldUser toRemove) {
        csldUserDao.makeTransient(toRemove);
    }

    @Override
    public List<CsldUser> getEditors() {
        Criterion criterion = Restrictions.eq("role", new Short("2"));
        return csldUserDao.findByCriteria(criterion);
    }

    @Override
    public List<CsldUser> getAdmins() {
        Criterion criterion = Restrictions.eq("role", new Short("3"));
        return csldUserDao.findByCriteria(criterion);
    }

    @Override
    public CsldUser getWithMostComments() {
        return csldUserDao.getWithMostComments();
    }

    @Override
    public CsldUser getWithMostAuthored() {
        return csldUserDao.getWithMostAuthored();
    }

    @Override
    public CsldUser authenticate(String username, String password) {
        return csldUserDao.authenticate(username, password);
    }

    @Override
    public List<CsldUser> getAuthorsByBestGame() {
        return csldUserDao.getAuthorsByBestGame();
    }

    @Override
    public List<CsldUser> getOrderedByName() {
        return csldUserDao.getOrderedByName();
    }

    @Override
    public List<CsldUser> getOrderedByComments() {
        return csldUserDao.getOrderedByComments();
    }

    @Override
    public List<CsldUser> getOrderedByPlayed() {
        return csldUserDao.getOrderedByPlayed();
    }

    @Override
    public List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        return csldUserDao.getByAutoCompletable(autoCompletable);
    }

    @Override
    public List<CsldUser> getOrderedUsersByName() {
        return csldUserDao.getOrderedUsersByName();
    }

    @Override
    public List<CsldUser> getOrderedUsersByComments() {
        return csldUserDao.gerOrderedUsersByComments();
    }

    @Override
    public List<CsldUser> getOrderedUsersByPlayed() {
        return csldUserDao.getOrderedUsersByPlayed();
    }

}
