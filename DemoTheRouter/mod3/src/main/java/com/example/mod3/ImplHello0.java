package com.example.mod3;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

@ServiceProvider(returnType = Hello1Provider.class)
public class ImplHello0 implements Hello1Provider {

    public ImplHello0() {
    }


    @Override
    public String test1() {
        return "test1";
    }

    @Override
    public String test2() {
        return "test2";
    }

    @Override
    public int test1ParamA() {
        return 0;
    }

    @Override
    public int test1ParamB() {
        return 0;
    }

    @Override
    public String test1ParamC() {
        return "";
    }
}
