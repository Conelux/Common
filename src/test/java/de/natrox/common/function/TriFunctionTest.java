package de.natrox.common.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TriFunctionTest {

    @Test
    void applyTest1() {
        TriFunction<Integer, Integer, Integer, Integer> function = this::doMath;
        assertEquals(28, function.apply(5, 1, 7), "Function should return 28 for 5 + 2*1 + 3*7.");
    }

    @Test
    void applyTest2() {
        TriFunction<Integer, Long, Double, String> function = this::concat;
        assertEquals("12345678.9", function.apply(12, 34567L, 8.9D), "Function should return \"12345678.9\" as 12, 334567L, 8.9D get combined.");
    }

    @Test
    void nullTest() {
        TriFunction<Integer, Integer, Integer, Integer> function = this::doMath;
        assertThrows(NullPointerException.class, () -> function.apply(null, null, null), "Function should throw a NullPointerException if the arguments are null.");
    }

    int doMath(int a, int b, int c) {
        return a + 2 * b + 3 * c;
    }

    String concat(Object a, Object b, Object c) {
        return a.toString() + b.toString() + c.toString();
    }
}
