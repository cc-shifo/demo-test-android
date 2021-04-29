package com.demo.demopaymodule.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class GsonUtils {

    public class BigDecimalSerializer implements JsonSerializer<BigDecimal> {
        @Override
        public JsonElement serialize(BigDecimal bigDecimal, Type type, JsonSerializationContext jsonSerializationContext) {
            if(bigDecimal == null){
                return null;
            }else{
                return new JsonPrimitive(bigDecimal.stripTrailingZeros().toPlainString());
            }
        }
    }

    Gson gson =new GsonBuilder()

            /*.serializeNulls()*/

            /*.setDateFormat("yyyy-MM-dd")*/

            .registerTypeAdapter(BigDecimal.class,new BigDecimalSerializer())

            .create();
}
