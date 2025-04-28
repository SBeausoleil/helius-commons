package systems.helius.commons.tests;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerCallCheckTest {

    @Test
    void accept() {
        var consumer = new ConsumerCallCheck<String>();
        consumer.accept("hello");
        assertTrue(consumer.wasCalled());
    }

    @Test
    void bend() {
        var consumer = new ConsumerCallCheck<String>();
        Function<Consumer<Annotation>, Long> functionNotTakingStringConsumer = c -> {
            c.accept(null);
            return 0L;
        };
        functionNotTakingStringConsumer.apply(consumer.bend());
        assertTrue(consumer.wasCalled());
    }

    @Test
    void bendInt() {
        var consumer = new ConsumerCallCheck<String>();
        Function<IntConsumer, Long> functionNotTakingStringConsumer = c -> {
            c.accept(0);
            return 0L;
        };
        functionNotTakingStringConsumer.apply(consumer.bendInt());
        assertTrue(consumer.wasCalled());
    }

    @Test
    void bendLong() {
        var consumer = new ConsumerCallCheck<String>();
        Function<LongConsumer, Long> functionNotTakingStringConsumer = c -> {
            c.accept(0L);
            return 0L;
        };
        functionNotTakingStringConsumer.apply(consumer.bendLong());
        assertTrue(consumer.wasCalled());
    }

    @Test
    void bendDouble() {
        var consumer = new ConsumerCallCheck<String>();
        Function<DoubleConsumer, Long> functionNotTakingStringConsumer = c -> {
            c.accept(0.0);
            return 0L;
        };
        functionNotTakingStringConsumer.apply(consumer.bendDouble());
        assertTrue(consumer.wasCalled());
    }

    @Test
    void wasCalledExactly() {
        var consumer = new ConsumerCallCheck<String>();
        assertTrue(consumer.wasCalledExactly(0));
        consumer.accept("hello");
        assertTrue(consumer.wasCalledExactly(1));
        consumer.accept("hello");
        assertTrue(consumer.wasCalledExactly(2));
    }

    @Test
    void wasCalledOnce() {
        var consumer = new ConsumerCallCheck<String>();
        assertFalse(consumer.wasCalledOnce());
        consumer.accept("hello");
        assertTrue(consumer.wasCalledOnce());
        consumer.accept("hello");
        assertFalse(consumer.wasCalledOnce());
    }

    @Test
    void getNCalls() {
        var consumer = new ConsumerCallCheck<String>();
        assertEquals(0, consumer.getNCalls());
        consumer.accept("hello");
        assertEquals(1, consumer.getNCalls());
        consumer.accept("hello");
        assertEquals(2, consumer.getNCalls());
    }

    @Test
    void reset() {
        var consumer = new ConsumerCallCheck<String>();
        consumer.accept("hello");
        assertEquals(1, consumer.getNCalls());
        consumer.reset();
        assertEquals(0, consumer.getNCalls());
    }
}