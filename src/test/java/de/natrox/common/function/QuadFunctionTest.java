package de.natrox.common.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuadFunctionTest {

    @Test
    void applyTest1() {
        QuadFunction<Integer, Integer, Integer, Integer, Integer> quadFunction = this::doMath;
        assertEquals(38, quadFunction.apply(5, 1, 7, 2));
    }

    @Test
    void applyTest2() {
        QuadFunction<Integer, Long, Boolean, Double, String> quadFunction = this::concat;
        assertEquals("1234567true8.9", quadFunction.apply(12, 34567L, true, 8.9D));
    }

    @Test
    void nullTest() {
        QuadFunction<Integer, Integer, Integer, Integer, Integer> quadFunction = this::doMath;
        assertThrows(NullPointerException.class, () -> quadFunction.apply(null, null, null, null));
    }

    int doMath(int a, int b, int c, int d) {
        return a + 2 * b + 3 * c + 5 * d;
    }

    String concat(Object a, Object b, Object c, Object d) {
        return a.toString() + b.toString() + c.toString() + d.toString();
    }
}
