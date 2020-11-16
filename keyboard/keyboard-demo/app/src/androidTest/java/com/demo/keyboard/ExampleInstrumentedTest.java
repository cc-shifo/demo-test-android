package com.demo.keyboard;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.demo.keyboard", appContext.getPackageName());
    }

    @Test
    public void testCurrencyZeroFormat() {
        Currency currency = Currency.getInstance(Locale.CHINA);
        int digitNum = currency.getDefaultFractionDigits();
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);
        numberFormat.setMaximumFractionDigits(digitNum);
        numberFormat.setMinimumFractionDigits(digitNum);
        numberFormat.setMaximumIntegerDigits(12-digitNum);
        numberFormat.setMinimumIntegerDigits(1);
        try {
            double a = 0 / Math.pow(10, digitNum);
            System.out.println("amt: " + numberFormat.format(a));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
