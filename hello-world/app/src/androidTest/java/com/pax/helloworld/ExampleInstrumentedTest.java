package com.pax.helloworld;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
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

        assertEquals("com.pax.helloworld", appContext.getPackageName());
    }

    @Test
    public void testLongToFloat() {
        String l = "123";
        float v = 0.0f;
        try {
            v = Float.parseFloat(l);
        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }

        System.out.println("val =" + v);

    }

    @Test
    public void testString() {
        long amount = 123456789L;
        float d = amount / 100.00f;
        String v = String.valueOf(d);
        System.out.println("Hello world");
        System.out.println("val =" + v);
        long decimal = 1;
        String a1 = String.format(Locale.US,"%%0%dd", decimal);
        System.out.println("val =a1: " + a1);
        String s1 =  String.format(Locale.US, a1, decimal);
        System.out.println("val =s1: " + s1);
        decimal = 2;
        s1 =  String.format(Locale.US, String.format(Locale.US,"%%0%dd", decimal), decimal);
        System.out.println("val =s1: " + s1);

        decimal = 3;
        s1 =  String.format(Locale.US, String.format(Locale.US,"0.%%0%dd", decimal), decimal);
        System.out.println("val =s1: " + s1);

        long amt = 1;
        Currency currency = Currency.getInstance(Locale.CHINA);
        int digitNum = currency.getDefaultFractionDigits();
        BigDecimal bigDecimal = new BigDecimal(amt);
        bigDecimal = bigDecimal.movePointLeft(digitNum);
        s1 = bigDecimal.toString();
        System.out.println("val =amt: " + s1);

        amt = 10;
        currency = Currency.getInstance(Locale.CHINA);
        digitNum = currency.getDefaultFractionDigits();
        bigDecimal = new BigDecimal(amt);
        bigDecimal = bigDecimal.movePointLeft(digitNum);
        s1 = bigDecimal.toString();
        System.out.println("val =amt: " + s1);

        amt = 110;
        currency = Currency.getInstance(Locale.CHINA);
        digitNum = currency.getDefaultFractionDigits();
        bigDecimal = new BigDecimal(amt);
        bigDecimal = bigDecimal.movePointLeft(digitNum);
        s1 = bigDecimal.toString();
        System.out.println("val =amt: " + s1);


        String sAmt = "123456789.123456789";
        currency = Currency.getInstance(Locale.CHINA);
        digitNum = currency.getDefaultFractionDigits();
        bigDecimal = new BigDecimal(sAmt);
        bigDecimal = bigDecimal.movePointRight(digitNum);
        System.out.println("val =sAmt: " + bigDecimal.longValue());
        sAmt = "123456789.129456789";
        currency = Currency.getInstance(Locale.CHINA);
        digitNum = currency.getDefaultFractionDigits();
        bigDecimal = new BigDecimal(sAmt);
        bigDecimal = bigDecimal.movePointRight(digitNum);
        System.out.println("val =sAmt: " + bigDecimal.longValue());

        double dV = 123456789.123456789;
        System.out.println("val =d: " + Double.valueOf(dV * 100).doubleValue());

        float amtF = 0.0f;
        currency = Currency.getInstance(Locale.CHINA);
        digitNum = currency.getDefaultFractionDigits();
        bigDecimal = new BigDecimal(amtF);
        bigDecimal = bigDecimal.movePointRight(digitNum);
        System.out.println("val =amtF: " + bigDecimal.longValue());

        dV = 0.0;
        currency = Currency.getInstance(Locale.CHINA);
        digitNum = currency.getDefaultFractionDigits();
        bigDecimal = new BigDecimal(dV);
        bigDecimal = bigDecimal.movePointRight(digitNum);
        System.out.println("val =amtF: " + bigDecimal.longValue());
    }
}
