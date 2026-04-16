package com.example.aggregationlib

import com.example.aarhelloworld1.AarHelloWorld1
import com.example.aarhelloworld2.AarHelloWorld2
import com.example.aarhelloworld3.AarHelloWorld3

class ApiLocalAar {
    companion object {
        @JvmStatic
        fun helloWorld1(): String {
            return ApiLocalAar::class.java.name + ", " + AarHelloWorld1.getHello()
        }

        @JvmStatic
        fun helloWorld2(): String {
            return ApiLocalAar::class.java.name +  ", " + AarHelloWorld2.getHello()
        }

        @JvmStatic
        fun helloWorld3(): String {
            return ApiLocalAar::class.java.name +  ", " + AarHelloWorld3.getHello()
        }
    }
}