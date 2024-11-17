package systems.helius.commons.reflection;

import org.junit.jupiter.api.Test;
import systems.helius.commons.types.DataClassWithoutGetters;
import systems.helius.commons.types.Foo;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BeanIntrospectorTest {
    @Test
    void legacy_WhenSeekInDataClass_GivenSeekWithoutGettersAndTargetInt_ThenFindAllInstances() throws IllegalAccessException {
        int first = 1;
        int second = 6;
        DataClassWithoutGetters simple = new DataClassWithoutGetters(
                first,
                second,
                "Hello",
                "World",
                9L,
                -15,
                first
        );
        Set<Integer> found = BeanIntrospector.seekIn(int.class, simple, MethodHandles.lookup());
        assertEquals(2, found.size());
        assertTrue(found.contains(first));
        assertTrue(found.contains(second));
    }

    @Test
    void WhenSeekInDataClass_GivenSeekWithoutGettersAndTargetInt_ThenFindAllInstances() throws IllegalAccessException {
        int first = 1;
        int second = 6;
        DataClassWithoutGetters simple = new DataClassWithoutGetters(
                first,
                second,
                "Hello",
                "World",
                9L,
                -15,
                first
        );
        Set<Integer> found = new BeanIntrospector().seek(int.class, simple, MethodHandles.lookup());
        assertEquals(2, found.size());
        assertTrue(found.contains(first));
        assertTrue(found.contains(second));
    }

    @Test
    void WhenSeekInDataClass_GivenSimpleClass_ThenFindAllInstances() throws IllegalAccessException {
        int second = 6;
        var foo = new Foo(second, "Hello");
        Set<Integer> found = new BeanIntrospector().seek(int.class, foo, MethodHandles.lookup());
        assertEquals(1, found.size());
        assertTrue(found.contains(second));
    }
}