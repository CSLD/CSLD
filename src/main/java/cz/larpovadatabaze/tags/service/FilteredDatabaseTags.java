package cz.larpovadatabaze.tags.service;

import cz.larpovadatabaze.entities.Label;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides tags filtered based on the filter.
 */
public class FilteredDatabaseTags implements Tags {
    private Tags sourceTags;
    private Filter applied;

    public FilteredDatabaseTags(Tags sourceTags, Filter applied) {
        this.sourceTags = sourceTags;
        this.applied = applied;
    }

    @Override
    public Collection<Label> all() {
        Collection<Label> all = sourceTags.all();
        Collection<Label> filtered = new ArrayList<>();
        for(Label label: all) {
            if(applied.name != null && label.getName().equalsIgnoreCase(applied.name)) {
                filtered.add(label);
            }
        }
        return filtered;
    }

    public static class Filter {
        private String name;

        public Filter(String name) {
            this.name = name;
        }
    }
}
