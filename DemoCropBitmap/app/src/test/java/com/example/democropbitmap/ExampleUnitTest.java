package com.example.democropbitmap;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Locale;

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
    public void testStringFormat() {
        double a = 2542662.5445144004;

        String str = String.format(Locale.US, "%.3f", a);
        System.out.println("testStringFormat: " + str);

        double a1 = 2542662.54;
        String str1 = String.format(Locale.US, "%.3f", a1);
        System.out.println("testStringFormat1: " + str1);

        double a2 = 2.54;
        String str2 = String.format(Locale.US, "%.3f", a2);
        System.out.println("testStringFormat2: " + str2);

        double a3 = 2.0;
        String str3 = String.format(Locale.US, "%.3f", a3);
        System.out.println("testStringFormat3: " + str3);

        double a4= 2542662.544499999;
        String str4 = String.format(Locale.US, "%.3f", a4);
        System.out.println("testStringFormat4: " + str4);
    }
}