package systems.helius.commons.types;

/**
 * A pair of values
 * @param left
 * @param right
 * @param <X> type of the left value
 * @param <Y> type of the right value
 */
public record Pair<X, Y>(X left, Y right) {}
