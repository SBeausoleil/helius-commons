package systems.helius.commons.tests;

import java.util.function.Consumer;

/**
 * A simple consumer that counts the number of times it has been called.
 *
 * @param <T> the type of the input to the operation
 */
public class ConsumerCallCheck<T> implements Consumer<T> {
    private int nCalls = 0;

    @Override
    public void accept(T t) {
        nCalls++;
    }

    /**
     * Bend the consumer to accept any type of input.
     * @return a consumer that accepts any type of input
     * @param <O> the type of the input to the operation
     */
    public <O> Consumer<O> bend() {
        return o -> nCalls++;
    }

    /**
     * Check if the consumer was called at least once.
     *
     * @return true if the consumer was called at least once, false otherwise
     */
    public boolean wasCalled() {
        return nCalls > 0;
    }

    /**
     * Check if the consumer was called exactly a certain number of times.
     *
     * @param exactly the number of calls to check for
     * @return true if the consumer was called exactly the specified number of times, false otherwise
     */
    public boolean wasCalledExactly(int exactly) {
        return nCalls == exactly;
    }

    /**
     * Check if the consumer was called exactly once.
     *
     * @return true if the consumer was called exactly once, false otherwise
     */
    public boolean wasCalledOnce() {
        return nCalls == 1;
    }

    /**
     * Returns the number of times the consumer was called.
     * @return the number of calls
     */
    public int getNCalls() {
        return nCalls;
    }

    /**
     * Reset the call count to zero.
     */
    public void reset() {
        nCalls = 0;
    }
}
