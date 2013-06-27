package org.apache.wicket.extensions.ajax.markup.html.autocomplete;

import cz.larpovadatabaze.services.GenericService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.5.13
 * Time: 9:07
 */
public class RepeatableInputPanel<T extends IAutoCompletable> extends Panel {
    final protected List<T> data = new ArrayList<T>();

    protected IFactory<T> factory;
    protected GenericService<T> service;

    public RepeatableInputPanel(String id,

                                final IModel<T> model,
                                final IFactory<T> factory,
                                final IValidator<T> validator,

                                GenericService<T> service) {
        super(id, model);
        this.factory = factory;
        this.service = service;

        data.add(factory.create());
        setOutputMarkupId(true);

        final ListView<T> repeatables =
                new RepeatableListView<T>("repeatableInputList", data, factory, validator, service);
        repeatables.setOutputMarkupId(true);
        repeatables.setReuseItems(true);

        add(repeatables);
    }

    public List<T> getData() {
        return data;
    }

    private Panel getThisPanel(){
        return this;
    }

    private class RepeatableListView<T extends IAutoCompletable> extends ListView<T>{
        protected IFactory<T> factory;
        protected IValidator<T> validator;
        protected GenericService<T> service;

        protected List<GenericModel<T>> models;

        public RepeatableListView(String id,
                                  List<T> data,
                                  IFactory<T> factory,
                                  IValidator<T> validator,
                                  GenericService<T> service) {
            super(id, data);

            models= new ArrayList<GenericModel<T>>();
            this.factory = factory;
            this.validator = validator;
            this.service = service;


        }

        @Override
        protected void populateItem(ListItem<T> item) {
            T modelObject = item.getModelObject();
            GenericModel<T> model = new GenericModel<T>(modelObject);
            models.add(model);

            final RepeatableInput<T> repeatable = new RepeatableInput<T>(
                    "repeatableInput",
                    model,
                    factory.getClassForConverter(),
                    service
            );
            repeatable.add(validator);

            ComponentFeedbackMessageFilter repeatableFilter = new ComponentFeedbackMessageFilter(repeatable);
            final FeedbackPanel repeatableFeedback = new FeedbackPanel("repeatableFeedback", repeatableFilter);
            repeatableFeedback.setOutputMarkupId(true);

            repeatable.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                    List<T> modelList = (List<T>) getList();
                    boolean addNew = true;
                    for(GenericModel<T> t: models) {
                        if(t == null || t.getObject() == null || t.getObject().getAutoCompleteData() == null) {
                            addNew = false;
                        }
                    }
                    if(addNew) {
                        modelList.add(factory.create());
                        ajaxRequestTarget.add(getThisPanel());
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, RuntimeException e){
                    super.onError(target, e);
                    target.add(repeatableFeedback);
                }
            });
            item.add(repeatableFeedback);

            item.add(repeatable);
        }
    }
}
