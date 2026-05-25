package com.example.demosetlanguage.data;

// TestN<User1, User2>
public class TestNBaseResp<T> extends TestBaseResp<T> {
    private static final String TAG = "TestNBaseResp";
    public String printName() {
        return getDataClass().toString();
    }
}
