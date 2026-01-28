package com.example.mod3;

import androidx.annotation.NonNull;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

// @ServiceProvider(returnType = Hello1Provider.class, params = {Integer.class, Integer.class, String.class})
public class ImplHello3 implements Hello1Provider {
    private int a;
    private int b;
    private String s;

    public ImplHello3(int a, int b, String s) {
        this.a = a;
        this.b = b;
        this.s = s;
    }

    @Override
    public String test1() {
        return "ImplHello3#test1";
    }

    @Override
    public String test2() {
        return "ImplHello3#test2";
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

    @NonNull
    @Override
    public String toString() {
        return  "ImplHello3, a=" + a + ", a=" + b + ", c=" + s;
    }
}
