package systems.helius.commons.collections;

import java.util.*;
import java.util.function.Predicate;

public final class IteratorUtils {
    private IteratorUtils() {}

    public static <T> List<T> drain(Iterator<T> it) {
        return drain(it, new ArrayList<T>());
    }

    public static <Buffer extends Collection<T>, T> Buffer drain(Iterator<T> it, Buffer into) {
        while (it.hasNext()) {
            into.add(it.next());
        }
        return into;
    }


    /**
     * Drains the iterator until the condition is met, including the element that met the endCondition.
     * @param endCondition the drain operation stops once an element is read that meets the predicate.
     * @return the buffer
     * @param <T> type of the iterator content.
     */
    public static <T> List<T> drainUntil(Iterator<T> it, Predicate<T> endCondition) {
        return drainUntil(it, new ArrayList<T>(), endCondition);
    }


    /**
     * Drains the iterator until the condition is met, including the element that met the endCondition.
     * @param buffer will receive the elements
     * @param endCondition the drain operation stops once an element is read that meets the predicate.
     * @return the buffer
     * @param <T> type of the iterator content.
     */
    public static <Buffer extends Collection<T>, T> Buffer drainUntil(Iterator<T> it, Buffer buffer, Predicate<T> endCondition) {
        while (it.hasNext()) {
            T current = it.next();
            buffer.add(current);
            if (endCondition.test(current)) break;
        }
        return buffer;
    }
}
