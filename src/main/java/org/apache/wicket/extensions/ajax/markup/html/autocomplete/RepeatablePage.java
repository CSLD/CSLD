package org.apache.wicket.extensions.ajax.markup.html.autocomplete;

import cz.larpovadatabaze.components.page.CsldBasePage;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.5.13
 * Time: 15:03
 */
public class RepeatablePage extends CsldBasePage {
    public RepeatablePage(){
        super();

        add(new RepeatableForm("repeatableForm"));

    }
}
