package com.example.demokmlparser;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.demokmlparser", appContext.getPackageName());
    }

    @Test
    public void testRegular() {
        String text = "abc\t1\t\t2\n" +
                "\n" +
                "2n\n" +
                "\n" +
                "\n" +
                "3n\n" +
                "sp1 sp2  sp3    sp4    !  ";
        text = text.replaceAll("[\\t|\\f|\\r|\\n]+", "");
        text = text.trim();
        text = text.replaceAll("\\s+", " ");
        System.out.println("val =amtF: @" + text);
    }
}