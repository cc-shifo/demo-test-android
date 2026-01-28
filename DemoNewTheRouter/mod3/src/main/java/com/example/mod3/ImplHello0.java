package com.example.mod3;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

// @ServiceProvider(returnType = Hello1Provider.class)
public class ImplHello0 implements Hello1Provider {

    public ImplHello0() {
    }


    public String test1() {
        return "test1";
    }

    public String test2() {
        return "test2";
    }

    public int test1ParamA() {
        return 0;
    }

    public int test1ParamB() {
        return 0;
    }

    public String test1ParamC() {
        return "";
    }
}
