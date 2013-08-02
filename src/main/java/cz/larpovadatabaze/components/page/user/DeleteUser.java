package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.services.*;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.5.13
 * Time: 13:42
 */
public class DeleteUser extends CsldBasePage {
    @SpringBean
    private CsldUserService csldUserService;
    @SpringBean
    private PersonService personService;
    @SpringBean
    private CommentService commentService;
    @SpringBean
    private RatingService ratingService;
    @SpringBean
    private LabelService labelService;

    public DeleteUser(PageParameters params){
        Integer userId = params.get("id").to(Integer.class);

        CsldUser csldUser = csldUserService.getById(userId);
        if(csldUser != null) {
            List<Comment> comments = csldUser.getCommented();
            for(Comment comment: comments){
                commentService.remove(comment);
            }

            Person person = csldUser.getPerson();
            List<Rating> ratings = csldUser.getRated();
            for(Rating rating: ratings){
                ratingService.remove(rating);
            }

            List<Label> labels = csldUser.getLabelsAuthor();
            for(Label label: labels){
                labelService.remove(label);
            }
            csldUserService.remove(csldUser);

            personService.remove(person);
        }

        throw new RestartResponseException(ListUser.class);
    }
}
