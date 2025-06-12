package com.example.mod3;

import com.example.modinter.Hello1Provider;
import com.therouter.inject.ServiceProvider;

public class TestMethod {
    @ServiceProvider
    public static Hello1Provider test0() {

        return new ImplHello0();
    }

    @ServiceProvider(returnType = Hello1Provider.class, params = {Integer.class})
    public static Hello1Provider test1(int a) {

        return new ImplHello1(a);
    }

    @ServiceProvider(returnType = Hello1Provider.class, params = {Integer.class, Integer.class})
    public static Hello1Provider test2(int a, int b) {

        return new ImplHello2(a, b);
    }

    @ServiceProvider(returnType = Hello1Provider.class, params = {Integer.class, Integer.class, String.class})
    public static Hello1Provider test3(int a, int b, String c) {

        return new ImplHello3(a, b, c);
    }
}
