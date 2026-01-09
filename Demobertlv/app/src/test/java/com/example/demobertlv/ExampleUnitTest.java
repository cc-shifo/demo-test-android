package com.example.demobertlv;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTagLength() {
        int n = (int) Math.ceil(Math.log(129) / Math.log(256));
        System.out.println("n = " + n);
    }
}