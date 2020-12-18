package com.example.testedit;

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
        assertEquals("com.example.testedit", appContext.getPackageName());
    }

    public enum  EMVTransactionType {
        GOOD_SERVICES((byte) 0x00),
        CASH((byte) 0x01),
        VOID((byte) 0x20),
        REFUNDS((byte) 0x20),

        PRE_AUTH((byte)0x03),   /* request*/
        PRE_AUTH_VOID((byte)0x20),   /* request*/
        PRE_AUTH_COMPLETE((byte)0x00),  /* request*/
        PRE_AUTH_COMPLETE_VOID((byte)0x20),  /* request*/



        BALANCE_INQUIRY((byte) 0x31);

        private byte code;

        EMVTransactionType(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return code;
        }

    }

    @Test
    public void TestIfEnumEqual() {
        if (EMVTransactionType.PRE_AUTH_COMPLETE == EMVTransactionType.GOOD_SERVICES) {
            System.out.println("PRE_AUTH_COMPLETE((byte)0x00) == GOOD_SERVICES((byte) 0x00)");
        } else {
            System.out.println("PRE_AUTH_COMPLETE((byte)0x00) != GOOD_SERVICES((byte) 0x00)");
        }
    }
}