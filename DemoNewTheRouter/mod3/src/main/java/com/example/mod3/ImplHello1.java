package com.example.mod3;

import androidx.annotation.NonNull;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

// @ServiceProvider(
//        returnType = Hello1Provider.class
// )
public class ImplHello1 implements Hello1Provider {
    private int a;

    public ImplHello1(int a) {
        this.a = a;
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
        return a * 2;
    }

    @Override
    public String test1ParamC() {
        return "ImplHello1#test1ParamC";
    }

    @NonNull
    @Override
    public String toString() {
        return "ImplHello1#toString, a=" + a + ", b(2a)=" + a * 2;
    }
}
