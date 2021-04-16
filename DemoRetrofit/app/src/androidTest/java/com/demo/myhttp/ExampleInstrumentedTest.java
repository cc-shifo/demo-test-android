package com.demo.myhttp;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

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
        assertEquals("com.demo.myhttp", appContext.getPackageName());
    }

    @Test
    public void testHttpBaseUrlRegular() {
//        NSString *regulaStr = @"((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})
//        (:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,
//        4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";


//        NSString *regulaStr = @"\\bhttps?://[a-zA-Z0-9\\-.]+(?::(\\d+))?(?:(?:/[a-zA-Z0-9\\-
//        ._?,'+\\&%$=~*!():@\\\\]*)+)?";


        String url = "http://posmarket.oss-cn-hangzhou.aliyuncs" +
                ".com/325332331386634240?Expires=1618389051&OSSAccessKeyId" +
                "=LTAI4G53XFrCkVthuQPBDgwR&Signature=qFpBXak5eRBxPEcMrQ8OBmMTTU4%3D";
        //String reg = "http[s]?://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*/";
        String reg = "http[s]?://([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*)(\\.([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*)" +
                ")*/";
        String reg1 = "\\w";
        String[] list = url.split(reg);
        System.out.println(String.format("get base url from:\n%s", url));
        for (String s : list) {
            System.out.println(s);
        }
    }
}