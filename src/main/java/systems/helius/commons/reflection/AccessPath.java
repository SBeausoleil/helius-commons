package systems.helius.commons.reflection;

import systems.helius.commons.types.Pair;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 *
 * @param startingAt
 * @param path Last handle points to either:
 *             <ol>
 *              <li>The desired value type</li>
 *              <li>A location where the path may contain the desired value (supertype of target)</li>
 *              <li>A location where the path becomes uncertain due to generics or vague typing</li>
 *             </ol>
 */
public record AccessPath<T>(
        Class<T> startingAt,
        LinkedList<Pair<Field, VarHandle>> path,
        End leadsTo
) {
    public enum End {
        /**
         * The final value is what is sought.
         */
        DESIRED,
        /**
         * The path leads to an uncertain mid-point.
         */
        UNCERTAIN,
        /**
         * The path leads to an iterable mid-point.
         */
        ITERABLE
    }

    public AccessPath {
        if (path() == null || path().isEmpty()) {
            throw new IllegalArgumentException("Path must not be null or empty!");
        }
    }

    /**
     * Follow the path to its end value.
     * If at any point the path leads to a null-value,
     * the follow operation will terminate with a null value.
     * @param instance to start at, must be assignable to #startingAt().
     * @return where it ended along with the value at that location
     */
    public Pair<Field, Object> follow(T instance) {
        Object value = instance;
        Pair<Field, Object> endpoint = null;
        for (var midpoint : path) {
            value = midpoint.right().get(value);
            if (value == null) {
                endpoint = new Pair<>(midpoint.left(), null);
                break;
            }
        }
        if (endpoint == null) {
            // We followed the path to its natural end
            endpoint = new Pair<>(path().peekLast().left(), value);
        }
        return endpoint;
    }

    boolean checkFollowedToTheEnd(Pair<Field, Object> followResult) {
        return followResult.left() == path.peekLast().left();
    }
}
