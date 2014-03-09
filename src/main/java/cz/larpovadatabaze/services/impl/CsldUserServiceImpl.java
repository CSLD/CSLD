package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.CsldUserDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.request.resource.ResourceReference;
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

    @Autowired
    private ImageService imageService;

    private ResourceReference userIconReference;

    @Override
    public CsldUser getById(Integer id) {
        return csldUserDao.findById(id, false);
    }

    @Override
    public boolean saveOrUpdate(CsldUser user) {
        return csldUserDao.saveOrUpdate(user);
    }

    @Override
    public void flush() {
        csldUserDao.flush();
    }

    public List<CsldUser> getAuthorsByGames(){
        return csldUserDao.getAuthorsByGames();
    }

    @Override
    public List<CsldUser> getAuthorsByGames(long first, long amountPerPage) {
        return csldUserDao.getAuthorsByGames(first, amountPerPage);
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
    public List<CsldUser> getAuthorsByBestGame(long first, long amountPerPage) {
        return csldUserDao.getAuthorsByBestGame(first, amountPerPage);
    }

    @Override
    public List<CsldUser> getByAutoCompletable(String autoCompletable) throws WrongParameterException {
        return csldUserDao.getByAutoCompletable(autoCompletable);
    }

    @Override
    public List<CsldUser> getOrderedUsersByName(long first, long amountPerPage) {
        return csldUserDao.getOrderedUsersByName(first, amountPerPage);
    }

    @Override
    public List<CsldUser> getOrderedUsersByComments(long first, long amountPerPage) {
        return csldUserDao.gerOrderedUsersByComments(first, amountPerPage);
    }

    @Override
    public List<CsldUser> getOrderedUsersByPlayed(long first, long amountPerPage) {
        return csldUserDao.getOrderedUsersByPlayed(first, amountPerPage);
    }

    @Override
    public CsldUser getByEmail(String mail) {
        return csldUserDao.getByEmail(mail);
    }

    @Override
    public int getAmountOfAuthors() {
        return csldUserDao.getAmountOfAuthors();
    }

    @Override
    public int getAmountOfOnlyAuthors() {
        return csldUserDao.getAmountOfOnlyAuthors();
    }

    @Override
    public List<CsldUser> getFirstChoices(String startsWith, int maxChoices) {
        return csldUserDao.getFirstChoices(startsWith, maxChoices);
    }

    @Override
    public List<CsldUser> getAuthorsByName(long first, long amountPerPage) {
        return csldUserDao.getAuthorsByName(first, amountPerPage);
    }

    @Override
    public boolean isLoggedAtLeastEditor() {
        CsldUser user = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(user == null){
            return false;
        }
        return user.getRole() >= CsldRoles.getRoleByName("Editor");
    }

    @Override
    public ResourceReference getIconReference() {
        // Reference is singleton, lazy-inited
        synchronized(this) {
            if (userIconReference == null) {
                userIconReference = imageService.createImageTypeResourceReference(csldUserDao);
            }
        }
        return userIconReference;
    }
}
