package systems.helius.commons.reflection;

import org.junit.jupiter.api.Test;
import systems.helius.commons.types.*;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class BeanIntrospectorTest {
    private static FooGenerator fooGenerator = new FooGenerator();
    private static FooCollectionGenerator fooCollectionGenerator = new FooCollectionGenerator(fooGenerator);

    @Test
    void WhenSeekInt_GivenObjectWithInheritance_ThenAlsoFindInSuperclass() throws IllegalAccessException {
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
    void WhenSeekInt_GivenSimpleClass_ThenFindAll() throws IllegalAccessException {
        var foo = fooGenerator.generate();
        Set<Integer> found = new BeanIntrospector().seek(int.class, foo, MethodHandles.lookup());
        assertEquals(1, found.size());
        assertTrue(found.contains(foo.getA()));
    }

    @Test
    void WhenSeekInt_GivenCollectionsWrapper_ThenFindAll() throws IllegalAccessException {
        FooCollection fooCollection = fooCollectionGenerator.generate();
        Set<Foo> found = new BeanIntrospector().seek(Foo.class, fooCollection, MethodHandles.lookup());
        assertEquals(fooCollection.totalElements(), found.size());
    }

    @Test
    void WhenSeekIterable_GivenClassWithIterables_ThenFindIterables() throws IllegalAccessException {
        FooCollection fooCollection = fooCollectionGenerator.generate();
        Set<Iterable> found = new BeanIntrospector().seek(Iterable.class, fooCollection, MethodHandles.lookup());
        assertEquals(2, found.size());
    }

    @Test
    void WhenSeekObjectArrayContent_GivenObjectArray_ThenFindAll() throws IllegalAccessException {
        Foo[] arr = fooGenerator.generate(5).toArray(new Foo[0]);
        Set<Foo> found = new BeanIntrospector().seek(Foo.class, arr, MethodHandles.lookup());
        assertEquals(arr.length, found.size());
    }

    @Test
    void WhenSeekPrimitiveArrayContent_GivenPrimitiveArray_ThenFindAll() throws IllegalAccessException {
        int[] arr = ThreadLocalRandom.current().ints().limit(15).toArray();
        Set<Integer> found = new BeanIntrospector(
                new IntrospectionSettingsBuilder()
                        .withSafeAccessCheck(false)
                        .build()
        ).seek(int.class, arr, MethodHandles.lookup());
        assertEquals(arr.length, found.size());
    }
}