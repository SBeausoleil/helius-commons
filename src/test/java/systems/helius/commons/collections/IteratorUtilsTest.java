package systems.helius.commons.collections;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class IteratorUtilsTest {

    @Test
    void GivenIterator_WhenDrain_ThenFullyDrainsIt() {
        Set<Integer> randoms = ThreadLocalRandom.current().ints(15).boxed().collect(Collectors.toSet());

        Iterator<Integer> it = randoms.iterator();
        List<Integer> result = IteratorUtils.drain(it);

        assertFalse(it.hasNext());
        assertEquals(randoms.size(), result.size());
        assertTrue(result.containsAll(randoms));
    }

    @Test
    void GivenIterator_WhenDrainIntOBuffer_ThenBufferIsReturned() {
        List<Integer> initialContent = List.of(1, 5, 7);
        List<Integer> buffer = new ArrayList<>(initialContent);
        Set<Integer> toAdd = ThreadLocalRandom.current().ints(15).filter(i -> !buffer.contains(i)).boxed().collect(Collectors.toSet());

        Iterator<Integer> it = toAdd.iterator();
        List<Integer> result = IteratorUtils.drain(it, buffer);

        assertSame(buffer, result);
        assertEquals(initialContent.size() + toAdd.size(), buffer.size());
        assertTrue(buffer.containsAll(initialContent));
        assertTrue(buffer.containsAll(toAdd));
    }

    @Test
    void GivenEmptyIterator_WhenDrain_ThenEmptyListIsReturned() {
        List<Object> result = IteratorUtils.drain(Collections.emptyIterator());
        assertTrue(result.isEmpty());
    }

    @Test
    void GivenIteratorWithSoughtValue_WhenDrainUntil_ThenLastElementOfResultIsSoughtValue() {
        List<Integer> content = List.of(1, 5, 7);
        List<Integer> result = IteratorUtils.drainUntil(content.iterator(), i -> i == 5);
        assertEquals(2, result.size());
        assertEquals(5, result.get(1));
    }

    @Test
    void GivenIteratorWithoutSoughtValue_WhenDrainUntil_ThenNothingThrown() {
        assertDoesNotThrow(() -> IteratorUtils.drainUntil(List.of(1, 2, 3).iterator(), (i) -> false));
    }
}