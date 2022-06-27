package de.natrox.common.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThrowableFunctionTest {

    @Test
    void defaultApplyTest1() {
        ThrowableFunction<String, Character, IllegalArgumentException> function = this::firstChar;
        assertEquals('f', function.apply("foo"));
        assertEquals('b', function.apply("boo"));
    }

    @Test
    void defaultApplyTest2() {
        ThrowableFunction<Double, Double, StringIndexOutOfBoundsException> function = this::inverse;
        assertEquals(1D / 2, function.apply(2D));
        assertEquals(2D, function.apply(1D / 2));
    }

    @Test
    void defaultApplyTest3() {
        try {
            ThrowableFunction<Integer, Double, Exception> function = this::sqrt;
            assertEquals(4.0, function.apply(16));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void exceptionApplyTest1() {
        ThrowableFunction<String, Character, StringIndexOutOfBoundsException> function = this::firstChar;
        assertThrows(StringIndexOutOfBoundsException.class, () -> function.apply(""));
    }

    @Test
    void exceptionApplyTest2() {
        ThrowableFunction<Double, Double, IllegalArgumentException> function = this::inverse;
        assertThrows(IllegalArgumentException.class, () -> function.apply(0D));
    }

    @Test
    void exceptionApplyTest3() {
        ThrowableFunction<Integer, Double, Exception> function = this::sqrt;
        assertThrows(Exception.class, () -> function.apply(-1));
    }

    char firstChar(String s) {
        return s.charAt(0);
    }

    double inverse(double a) {
        if (a == 0)
            throw new IllegalArgumentException();
        return 1 / a;
    }

    double sqrt(int a) throws Exception {
        if (a < 0)
            throw new Exception();
        return Math.sqrt(a);
    }
}
