package systems.helius.commons.collections;

import org.junit.jupiter.api.Test;
import systems.helius.commons.types.Foo;
import systems.helius.commons.types.FooGenerator;
import systems.helius.commons.types.NumberWrapper;
import systems.helius.commons.types.NumberWrapperGenerator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BridgingIteratorTest {

    FooGenerator fooGenerator = new FooGenerator();
    NumberWrapperGenerator numberGenerator = new NumberWrapperGenerator();

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
        List<NumberWrapper> firstList = numberGenerator.generate(2);
        LinkedList<NumberWrapper> secondList = new LinkedList<>(numberGenerator.generate(5));
        List<NumberWrapper> thirdList = numberGenerator.generate(3);
        NumberWrapper target = secondList.getLast();

        var it = new BridgingIterator<>(firstList, secondList, thirdList);
        IteratorUtils.drainUntil(it, v -> v == target);
        it.remove();

        assertFalse(secondList.contains(target));
    }

    @Test
    void GivenMultipleSources_WhenRemoveAfterChangingActiveSubIteratorWithoutReadingTheNewOne_ThenRemoveLastOfPreviousIterator() {
        List<NumberWrapper> firstList = numberGenerator.generate(2);
        LinkedList<NumberWrapper> secondList = new LinkedList<>(numberGenerator.generate(5));
        List<NumberWrapper> thirdList = numberGenerator.generate(3);
        NumberWrapper target = secondList.getLast();
        target.setValue(950);

        var it = new BridgingIterator<>(firstList, secondList, thirdList);
        IteratorUtils.drainUntil(it, v -> v == target);
        assertSame(secondList, it.currentSource());
        //noinspection ResultOfMethodCallIgnored used to cause the side-effect of changing the current subiterator
        it.hasNext();
        assertSame(thirdList, it.currentSource());
        it.remove();

        assertFalse(secondList.contains(target));
    }
}