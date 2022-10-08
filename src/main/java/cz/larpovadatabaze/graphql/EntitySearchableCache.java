package cz.larpovadatabaze.graphql;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import cz.larpovadatabaze.common.IAutoCompletable;

/**
 * Caches objects and allows searching them.
 *
 * @param <T> Type of cached object
 */
public abstract class EntitySearchableCache<T extends IAutoCompletable> {
    private final static long CACHE_TTL = 60000; // 1 hour
    private long cacheExpiration = 0;
    private List<ObjectWithName<T>> cachedObjects;

    public static class ResultsWithTotal<T> {
        private final List<T> results;
        private final Integer total;

        public List<T> getResults() {
            return results;
        }

        public int getTotal() {
            return total;
        }

        public ResultsWithTotal(List<T> results, Integer total) {
            this.results = results;
            this.total = total;
        }
    }

    /**
     * Storage object
     */
    private static class ObjectWithName<T extends Object> {
        private final String name;
        private final String[] names;
        private final T entity;

        public T getEntity() {
            return entity;
        }

        private ObjectWithName(String name, T entity) {
            this.name = normalize(name);
            this.names = splitWords(this.name);
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

    private ResultsWithTotal<T> searchInternal(String query, int offset, int limit, boolean computeTotal) {
        if (cacheExpiration < new Date().getTime()) {
            // Re-fetch objects
            cachedObjects = getAll().stream().map(object -> new ObjectWithName<T>(object.getAutoCompleteData(), object)).collect(Collectors.toList());
            cacheExpiration = new Date().getTime() + CACHE_TTL;
        }

        var normalizedQuery = normalize(query);
        String[] terms = splitWords(normalizedQuery);
        // We need all entries when we have to compute total
        int needLen = computeTotal ? Integer.MAX_VALUE : offset + limit;

        // Add full-matching items first
        List<T> res = cachedObjects.stream().filter(
                game -> game.name.equals(normalizedQuery)
        ).map(game -> game.getEntity()).collect(Collectors.toList());

        // We want to have early exit when number of found objects reaches limit for performance reasons,
        // so that's why we can't use stream API here.
        for(ObjectWithName<T> candidate: cachedObjects) {
            // Add candidate when it matches but is not  exactly equal (because we added those in previous step)
            if (candidate.matches(terms) && !candidate.name.equals(normalizedQuery)) {
                res.add(candidate.getEntity());
                if (res.size() >= needLen) {
                    break;
                }
            }
        }

        if (res.size() < offset) {
            return new ResultsWithTotal<>(Collections.emptyList(), computeTotal ? res.size() : null);
        }

        return new ResultsWithTotal<>(res.subList(offset, Math.min(offset + limit, res.size())), computeTotal ? res.size() : null);
    }

    public ResultsWithTotal<T> searchWithTotal(String query, int offset, int limit) {
        return searchInternal(query, offset, limit, true);
    }

    public List<T> search(String query, int offset, int limit) {
        return searchInternal(query, offset, limit, false).results;
    }

    private static String normalize(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        return s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase();
    }

    /**
     * Take string and build list of terms from it
     *
     * @param s Input string
     *
     * @return List of normalized terms
     */
    private static String[] splitWords(String s) {
        return s.split(" +");
    }

    /**
     * @return All entities of given type
     */
    public abstract Collection<T> getAll();
}
