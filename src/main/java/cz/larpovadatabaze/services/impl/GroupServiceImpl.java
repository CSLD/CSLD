package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GroupDAO;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 18:18
 */
@Repository
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDAO groupDAO;

    @Override
    public void insert(CsldGroup group) {
        groupDAO.saveOrUpdate(group);
        groupDAO.flush();
    }

    @Override
    public List<CsldGroup> getAll() {
        return groupDAO.findAll();
    }

    @Override
    public List<CsldGroup> getUnique(CsldGroup example) {
        return groupDAO.findByExample(example, new String[]{});
    }

    @Override
    public List<CsldGroup> orderedByName() {
        return groupDAO.orderedByName();
    }

    @Override
    public void remove(CsldGroup toRemove) {
        groupDAO.makeTransient(toRemove);
    }

    @Override
    public CsldGroup getById(Integer id){
        return groupDAO.findById(id, false);
    }

    @Override
    public List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException {
        return groupDAO.getByAutoCompletable(groupName);
    }
}
