package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.GameDAO;
import cz.larpovadatabaze.dao.LabelDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.LabelService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Repository
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelDAO labelDAO;

    @Autowired
    private GameDAO gameDAO;

    public List<Label> getAll(){
        return labelDAO.findAll();
    }

    @Override
    public List<Label> getUnique(Label example) {
        return labelDAO.findByExample(example, new String[]{});
    }

    public List<Label> getRequired(){
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.eq("required", true);
        return labelDAO.findByCriteria(criterions);
    }

    public List<Label> getOptional(){
        Criterion[] criterions = new Criterion[1];
        criterions[0] = Restrictions.eq("required", false);
        return labelDAO.findByCriteria(criterions);
    }

    @Override
    public void update(Label label) {
        labelDAO.saveOrUpdate(label);
    }

    @Override
    public List<Label> getAuthorizedRequired(CsldUser authorizedTo) {
        List<Label> optionalLabels = getRequired();
        List<Label> labels = new ArrayList<Label>();
        for(Label label: optionalLabels){
            if(label.getAuthorized() != null && !label.getAuthorized()) {
                if(!label.getAddedBy().equals(authorizedTo)) {
                    continue;
                }
            }

            labels.add(label);
        }
        return labels;
    }

    @Override
    public List<Label> getAuthorizedOptional(CsldUser authorizedTo) {
        List<Label> optionalLabels = getOptional();
        List<Label> labels = new ArrayList<Label>();
        for(Label label: optionalLabels){
            if(label.getAuthorized() != null && !label.getAuthorized()) {
                if(!label.getAddedBy().equals(authorizedTo)) {
                    continue;
                }
            }

            labels.add(label);
        }
        return labels;
    }

    @Override
    public List<Label> getByAutoCompletable(String labelName) throws WrongParameterException {
        return labelDAO.getByAutoCompletable(labelName);
    }

    @Override
    public Label getByName(String name) {
        return labelDAO.findSingleByCriteria(Restrictions.eq("name",name));
    }

    @Override
    public boolean saveOrUpdate(Label label) {
        return labelDAO.saveOrUpdate(label);
    }

    @Override
    public Label getById(int filterLabel) {
        return labelDAO.findById(filterLabel, false);
    }

    @Override
    public void remove(Label toRemove) {
        // Remove label
        labelDAO.makeTransient(toRemove);
    }

    @Override
    public List<Label> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }
}
