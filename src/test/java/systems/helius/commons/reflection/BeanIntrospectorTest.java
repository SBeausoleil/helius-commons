package systems.helius.commons.reflection;

import com.sb.factorium.FactoryProvider;
import com.sb.factorium.RecordingFactory;
import com.sb.factorium.RecordingFactoryMaker;
import org.junit.jupiter.api.Test;
import systems.helius.commons.types.*;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

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
        int[] arr = ThreadLocalRandom.current().ints(5).toArray();
        Set<Integer> found = new BeanIntrospector().seek(int.class, arr, MethodHandles.lookup());
        assertEquals(arr.length, found.size());
    }

    @Test
    void WhenSeekObjectArrayContent_GivenNestedObjectArray_ThenFindAll() throws IllegalAccessException {
        RecordingFactory<String, Foo> recordingFactory = (RecordingFactory<String, Foo>) FactoryProvider.make(
                List.of(fooGenerator), FactoryProvider.DefaultKey.DEFAULT_KEY, new RecordingFactoryMaker(), false)
                .factory(Foo.class);
        Foo[][][][] multiDimensionalArray = new Foo[3][3][3][];
        for (int i = 0; i < multiDimensionalArray.length; i++) {
            for (int j = 0; j < multiDimensionalArray[i].length; j++) {
                for (int k = 0; k < multiDimensionalArray[i][j].length; k++) {
                    multiDimensionalArray[i][j][k] = recordingFactory.generate(3).toArray(new Foo[0]);
                }
            }
        }
        Set<Foo> foos = new BeanIntrospector().seek(Foo.class, multiDimensionalArray, MethodHandles.lookup());
        assertEquals(recordingFactory.getCreated().size(), foos.size());
    }

    @Test
    void WhenSeekPrimitiveArrayContent_GivenNestedPrimitiveArray_ThenFindAll() throws IllegalAccessException {
        final int LOWEST_LEVEL_SIZE = 3;
        int nGenerated = 0;
        long[][][][] multiDimensionalArray = new long[3][3][3][];
        for (int i = 0; i < multiDimensionalArray.length; i++) {
            for (int j = 0; j < multiDimensionalArray[i].length; j++) {
                for (int k = 0; k < multiDimensionalArray[i][j].length; k++) {
                    multiDimensionalArray[i][j][k] = ThreadLocalRandom.current().longs(LOWEST_LEVEL_SIZE).toArray();
                    nGenerated += LOWEST_LEVEL_SIZE;
                }
            }
        }
        Set<Long> found = new BeanIntrospector().seek(long.class, multiDimensionalArray, MethodHandles.lookup());
        assertEquals(nGenerated, found.size());
    }

    @Test
    void WhenSeekPrimitiveWrapper_GivenClassWithMixedPrimitivesAndWrappers_ThenOnlyFindWrappers() throws IllegalAccessException {
        int first = -15;
        int second = 16;
        DataClassWithoutGetters simple = new DataClassWithoutGetters(
                1,
                2,
                "Hello",
                "World",
                9L,
                first,
                second
        );
        Set<Integer> found = new BeanIntrospector().seek(Integer.class, simple, MethodHandles.lookup());
        assertEquals(2, found.size());
        assertTrue(found.contains(first));
        assertTrue(found.contains(second));
    }
}