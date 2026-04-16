package com.example.demofusedlib

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aarhelloworld1.AarHelloWorld1
import com.example.aarhelloworld2.AarHelloWorld2
import com.example.aarhelloworld3.AarHelloWorld3
import com.example.aggregationlib.ApiLocalAar

import com.example.demofusedlib.databinding.ActivityMainBinding
import com.example.libhelloworld1.LibHelloWorld1
import com.example.libhelloworld2.LibHelloWorld2

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView(R.layout.activity_main)
        setContentView(mBinding.root)

        mBinding.btnAar1.setOnClickListener {
            mBinding.helloWorld.text = AarHelloWorld1.getHello()
        }

        mBinding.btnAar2.setOnClickListener {
            mBinding.helloWorld.text = AarHelloWorld2.getHello()
        }

        mBinding.btnAar3.setOnClickListener {
            mBinding.helloWorld.text = AarHelloWorld3.getHello()
        }

        mBinding.btnLocalAarAll.setOnClickListener {
            mBinding.helloWorld.text = ApiLocalAar.helloWorld1()
            val sb = StringBuilder()
            sb.append(ApiLocalAar.helloWorld1()).append("\n")
                .append(ApiLocalAar.helloWorld2()).append("\n")
                .append(ApiLocalAar.helloWorld3())
            mBinding.helloWorld.text = sb.toString()
        }

        mBinding.btnLib1.setOnClickListener {
            mBinding.helloWorld.text = LibHelloWorld1.helloWorld()
        }

        mBinding.btnLib2.setOnClickListener {
            mBinding.helloWorld.text = LibHelloWorld2.helloWorld()
        }
    }
}