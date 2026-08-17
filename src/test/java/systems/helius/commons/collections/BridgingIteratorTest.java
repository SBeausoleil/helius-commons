package systems.helius.commons.collections;

import org.junit.jupiter.api.Test;
import systems.helius.commons.fixtures.Foo;
import systems.helius.commons.fixtures.FooGenerator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BridgingIteratorTest {

    FooGenerator fooGenerator = new FooGenerator();

    @Test
    void GivenMultipleSources_WhenNext_ThenReadAll() {
        List<Foo> firstSource = fooGenerator.generate(5);
        Set<Foo> secondSource = new HashSet<>(fooGenerator.generate(3));
        List<Foo> thirdSource = List.of(fooGenerator.generate(5).toArray(new Foo[0]));
        final int TOTAL_SIZE = firstSource.size() + secondSource.size() + thirdSource.size();

        BridgingIterator<Foo> it = new BridgingIterator<>(firstSource, secondSource, thirdSource);

        ArrayList<Foo> content = IteratorUtils.drain(it, new ArrayList<>(TOTAL_SIZE));
        assertEquals(TOTAL_SIZE, content.size());
        assertTrue(content.containsAll(firstSource));
        assertTrue(content.containsAll(secondSource));
        assertTrue(content.containsAll(thirdSource));
    }

    @Test
    void GivenSourceThatSupportsRemove_WhenRemove_ThenRemove() {
        LinkedList<Foo> list = new LinkedList<>(fooGenerator.generate(3));
        Foo first = list.peek();

        var it = new BridgingIterator<>(list);

        assertEquals(first, it.next());
        it.remove();
        assertFalse(list.contains(first));
        assertEquals(list.size(), IteratorUtils.drain(it).size());
    }

    @Test
    void GivenThreeSources_WhenRemoveLastOfSecondSource_ThenRemoveLastOfSecondSource() {
        List<Foo> firstList = fooGenerator.generate(2);
        LinkedList<Foo> secondList = new LinkedList<>(fooGenerator.generate(5));
        List<Foo> thirdList = fooGenerator.generate(3);
        Foo target = secondList.getLast();

        var it = new BridgingIterator<>(firstList, secondList, thirdList);
        IteratorUtils.drainUntil(it, v -> v == target);
        it.remove();

        assertFalse(secondList.contains(target));
    }

    /**
     * Guarantees the expected behavior of Iterator that when {@link Iterator#remove()} is called,
     * the previous returned value is removed even if the internal active iterator has changed.
     */
    @Test
    void GivenMultipleSources_WhenRemoveAfterChangingActiveSubIteratorWithoutReadingTheNewOne_ThenRemoveLastOfPreviousIterator() {
        List<Foo> firstList = fooGenerator.generate(2);
        LinkedList<Foo> secondList = new LinkedList<>(fooGenerator.generate(5));
        List<Foo> thirdList = fooGenerator.generate(3);
        Foo target = secondList.getLast();

        var it = new BridgingIterator<>(firstList, secondList, thirdList);
        IteratorUtils.drainUntil(it, v -> v == target);
        assertSame(secondList, it.currentSource());
        //noinspection ResultOfMethodCallIgnored used to cause the side-effect of changing the current subiterator
        it.hasNext();
        assertSame(thirdList, it.currentSource());
        it.remove();

        assertFalse(secondList.contains(target));
    }

    @Test
    void GivenEmptyMiddleSource_WhenNext_ThenSkipSilently() {
        List<Foo> firstList = fooGenerator.generate(1);
        List<Foo> secondList = Collections.emptyList();
        List<Foo> thirdList = fooGenerator.generate(3);

        var it = new BridgingIterator<>(firstList, secondList, thirdList);
        List<Foo> content = IteratorUtils.drain(it);

        assertEquals(firstList.size() + thirdList.size(), content.size());
        assertTrue(content.containsAll(firstList));
        assertTrue(content.containsAll(thirdList));
    }
}