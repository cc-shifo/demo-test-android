package com.example.modinter;

import com.therouter.inject.Singleton;

@Singleton
public interface Hello1Provider {
    String test1();

    String test2();

    int test1ParamA();
    int test1ParamB();
    String test1ParamC();

}
