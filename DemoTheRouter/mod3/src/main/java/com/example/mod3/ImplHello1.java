package com.example.mod3;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

@ServiceProvider(returnType = Hello1Provider.class, params = {Integer.class, Integer.class, String.class})
public class ImplHello1 implements Hello1Provider {
    private int a;
    private int b;
    private String s;
    public ImplHello1() {
    }

    public ImplHello1(int a, int b, String s) {
        this.a = a;
        this.b = b;
        this.s = s;
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
        return a;
    }

    @Override
    public int test1ParamB() {
        return b;

    }

    @Override
    public String test1ParamC() {
        return s;
    }
}
