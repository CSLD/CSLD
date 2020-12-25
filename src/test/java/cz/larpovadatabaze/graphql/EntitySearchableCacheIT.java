package cz.larpovadatabaze.graphql;

import cz.larpovadatabaze.common.components.multiac.IAutoCompletable;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EntitySearchableCacheIT {
    private static class TestEntity implements IAutoCompletable {
        private final int id;
        private final String name;

        private TestEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getAutoCompleteData() {
            return name;
        }
    }

    private static List<TestEntity> testEntities = Arrays.asList(new TestEntity[]{
            new TestEntity(1, "Hellsinglarp 2"),
            new TestEntity(2, "Hell on Wheels"),
            new TestEntity(3, "Hellsing párty"),
            new TestEntity(4, "Hellsinglarp"),
            new TestEntity(5, "Hellsinglarp 2012"),
            new TestEntity(6, "Hellsinglarp 3: Operace Siegfried"),
            new TestEntity(7, "Hellsing Larp V."),
            new TestEntity(8, "Hellcon 2001"),
    });

    private class TestEntitySearchableCache extends EntitySearchableCache<TestEntity> {
        @Override
        public Collection<TestEntity> getAll() {
            return testEntities;
        }
    }

    private final TestEntitySearchableCache cache = new TestEntitySearchableCache();

    private static String idString(List<TestEntity> list) {
        return list.stream().map(entry -> Integer.toString(entry.id)).collect(Collectors.joining(","));
    }

    @Test
    public void testHellWheels() {
        List<TestEntity> res = cache.search("He Whe", 0, 99);

        assertThat(idString(res), equalTo("2"));
    }

    @Test
    public void testHellOnWheels() {
        List<TestEntity> res = cache.search("He On Whe", 0, 99);

        assertThat(idString(res), equalTo("2"));
    }

    @Test
    public void testOnWheels() {
        List<TestEntity> res = cache.search("On Whe", 0, 99);

        assertThat(idString(res), equalTo("2"));
    }

    @Test
    public void testWheels() {
        List<TestEntity> res = cache.search("Wheels", 0, 99);

        assertThat(idString(res), equalTo("2"));
    }

    @Test
    public void testHellWheelsOn() {
        List<TestEntity> res = cache.search("He Whe On", 0, 99);

        assertThat(idString(res), equalTo(""));
    }

    @Test
    public void testIgnoringCaseAndAccents() {
        List<TestEntity> res = cache.search("HELL PARTY", 0, 99);

        assertThat(idString(res), equalTo("3"));
    }

    @Test
    public void testMultipleSpaces() {
        List<TestEntity> res = cache.search("hellsinglarp    2012", 0, 99);

        assertThat(idString(res), equalTo("5"));
    }

    @Test
    public void testPaging() {
        List<TestEntity> res = cache.search("hell", 2, 3);

        assertThat(idString(res), equalTo("3,4,5"));
    }

    @Test
    public void testAfterLastPage() {
        List<TestEntity> res = cache.search("2001", 2, 3);

        assertThat(idString(res), equalTo(""));
    }
}
