package com.example.mod3;

import androidx.annotation.NonNull;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

@ServiceProvider(returnType = Hello1Provider.class, params = {/*Integer.class, */Integer.class})
public class ImplHello2 implements Hello1Provider {
    private int a;
    private int b;
    private String s;

    public ImplHello2(String astr) {
        // this.a = a;
        // this.b = b;
        this.s = "ImplHello2";
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

    @NonNull
    @Override
    public String toString() {
        return a + ", " + b + "," + s;
    }
}
