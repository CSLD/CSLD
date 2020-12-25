package cz.larpovadatabaze.graphql;

import cz.larpovadatabaze.common.components.multiac.IAutoCompletable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Caches objects and allows searching them.
 *
 * @param <T> Type of cached object
 */
public abstract class EntitySearchableCache<T extends IAutoCompletable> {
    private final static long CACHE_TTL = 60000; // 1 hour
    private long cacheExpiration = 0;
    private List<ObjectWithName<T>> cachedObjects;

    /**
     * Storage object
     */
    private static class ObjectWithName<T extends Object> {
        private final String[] names;
        private final T entity;

        public T getEntity() {
            return entity;
        }


        private ObjectWithName(String name, T entity) {
            this.names = buildTerms(name);
            this.entity = entity;
        }

        public boolean matches(String[] term) {
            int termLen = term.length;

            for (int i = 0; i <= (names.length - term.length); i++) {
                int numMatches = 0;
                for (int n = i; (n <= (names.length - (termLen-numMatches))) && (numMatches < termLen); n++) {
                    if (names[n].startsWith(term[numMatches])) {
                        numMatches++;
                    }
                }

                if (numMatches == termLen) {
                    return true;
                }
            }

            return false;
        }
    }

    public List<T> search(String query, int offset, int limit) {
        if (cacheExpiration < new Date().getTime()) {
            // Re-fetch objects
            cachedObjects = getAll().stream().map(object -> new ObjectWithName<T>(object.getAutoCompleteData(), object)).collect(Collectors.toList());
            cacheExpiration = new Date().getTime() + CACHE_TTL;
        }

        String[] terms = buildTerms(query);
        int needLen = offset + limit;
        List<T> res = new ArrayList<>();
        // We want to have early exit when number of found objects reaches limit for performance reasons,
        // so that's why we can't use stream API here.
        for(ObjectWithName<T> candidate: cachedObjects) {
            if (candidate.matches(terms)) {
                res.add(candidate.getEntity());
                if (res.size() >= needLen) {
                    break;
                }
            }
        }

        if (res.size() < offset) {
            return Collections.emptyList();
        }

        return res.subList(offset, res.size());
    }

    /**
     * Take string and build list of terms from it
     *
     * @param s Input string
     *
     * @return List of normalized terms
     */
    private static String[] buildTerms(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.toLowerCase().split(" +");
    }

    /**
     * @return All entities of given type
     */
    public abstract Collection<T> getAll();
}
