/*
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2020-? Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *
 * Revision History:
 * Date	                 Author	                Action
 * 20200212 	         liujian                  Create
 */

package com.pax.helloworld.third;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SemiDataPkgParser {

    public SemiDataPkgParser() {
    }

    public SemiDataPkg fromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<SemiDataPkg>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String toJson(SemiDataPkg obj) {
        Gson gson = new Gson();
        Type type = new TypeToken<SemiDataPkg>() {
        }.getType();
        return gson.toJson(obj, type);
    }

    public String toJsonWithSerializeNull(SemiDataPkg obj) {
        Gson gson = new Gson().newBuilder().serializeNulls().create();
        Type type = new TypeToken<SemiDataPkg>() {
        }.getType();
        return gson.toJson(obj, type);
    }

    public String toJsonWithNonTypeToken(SemiDataPkg obj) {
        Gson gson = new Gson();
        return gson.toJson(obj, SemiDataPkg.class);
    }

    public SemiDataPkg fromJsonWithNonTypeToken(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SemiDataPkg.class);
    }
}
