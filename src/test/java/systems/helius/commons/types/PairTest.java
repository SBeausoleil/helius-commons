package systems.helius.commons.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void left() {
        String left = "hello";
        String right = "world";
        var pair = new Pair<>(left, right);
        assertEquals(left, pair.left());
    }

    @Test
    void right() {
        String left = "hello";
        String right = "world";
        var pair = new Pair<>(left, right);
        assertEquals(right, pair.right());
    }
}