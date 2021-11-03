package com.example.demoarcgis;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.demoarcgis", appContext.getPackageName());
    }

    @Test
    public void test() {
        if (isApproximatelyEqual(30.457103263, 114.479056907, 0)) {
            System.out.println("isApproximatelyEqual true");
        } else {
            System.out.println("isApproximatelyEqual false");
        }
    }

    private boolean isApproximatelyEqual(double latitude, double longitude, double heightLevel) {

        return (Double.compare(Math.abs(30.457103264 - latitude), 0.000001) <= 0
                && Double.compare(Math.abs(114.479056907 - longitude), 0.000001) <= 0
                && Double.compare(Math.abs(0 - heightLevel), 0.000001) <= 0);
    }

    @Test
    public void testTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS",
                Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String v = dateFormat.format(date) + TimeZone.getDefault().getID();
        System.out.println("dateFormat: " + v);

        v = dateFormat.format(date) + Locale.getDefault().getCountry();
        System.out.println("dateFormat: " + v);
    }

    @Test
    public void removeOdd() {
        System.out.println("原始数据：");
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            list.add(i);
            System.out.printf("%d ", i);
        }

        int size = list.size();

        Iterator<Integer> iterator = list.iterator();
        for (int i = 0; i < size; i++) {
            iterator.next();
            if (i % 2 == 0) {
                iterator.remove();
            }

        }
        System.out.println("\n删除后数据：");
        for (int v : list) {
            System.out.printf("%d ", v);
        }

        System.out.println("\n结束");
    }

    @Test
    public void removeEven() {

    }

    @Test
    public void test2DimensionArraySize() {
        double[][] mPointCache = new double[4096][2];

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);



        System.out.printf("DimensionArraySize: %d\n", mPointCache.length);
        System.out.printf("DimensionArraySize: limit=%d\n", byteBuffer.limit());
        System.out.printf("DimensionArraySize: position=%d\n", byteBuffer.position());
        byteBuffer.putDouble(1);
        System.out.printf("DimensionArraySize: position=%d\n", byteBuffer.position());
        System.out.printf("DimensionArraySize: hasArray=%d\n", byteBuffer.hasArray() ? 1 : 0);

    }
}