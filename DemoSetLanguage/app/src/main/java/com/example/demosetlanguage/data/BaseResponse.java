package com.example.demosetlanguage.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type
 * 只是一个 “标记接口”，没有任何方法。
 * 核心作用：作用：代表 “所有类型”，包括普通类、泛型、数组、通配符等。
 * 典型示例：Type tType = types[0];
 * 这里的 tType 只是一个引用，它的真实类型一定是下面 5 种中的一种：
 * Class（最常见，比如 User、String、Integer）
 * ParameterizedType（比如 List<User>）
 * TypeVariable（比如 T）
 * WildcardType（比如 ? extends User）
 * GenericArrayType（比如 T[]）
 * 在解析时，必须先判断 instanceof：
 * if (tType instanceof Class) {
 * <p>
 * } else if (tType instanceof ParameterizedType) {
 * <p>
 * } else if (tType instanceof TypeVariable) {
 * <p>
 * } else if (tType instanceof WildcardType) {
 * <p>
 * } else if (tType instanceof GenericArrayType) {
 * <p>
 * }
 * <p>
 * ParameterizedType
 * 核心作用：表示参数化类型（带泛型参数的类型），即泛型被具体类型 / 通配符填充后的类型
 * 典型示例：List<String>、Map<Integer, Long>
 * <p>
 * GenericArrayType
 * 核心作用：表示泛型数组类型（数组的组件类型是泛型类型，而非具体类）
 * 典型示例：T[]、List<String>[]
 * <p>
 * TypeVariable
 * 核心作用：表示类型变量（泛型定义时的占位符）
 * 典型示例：T（如 class Box<T> 中的 T）
 * <p>
 * WildcardType
 * 核心作用：表示通配符类型（泛型中的 ?、? extends X、? super X）
 * 典型示例：?、? extends Number、? super Integer
 *
 *
 * getClass().getGenericSuperclass()获取的是什么
 */
public class BaseResponse<T> {

    private String mMsg;
    private int mCode;

    private T mData;


    public BaseResponse() {
    }

    public BaseResponse(String msg, int code) {
        mMsg = msg;
        mCode = code;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    private Type type;

    public Type getType() {
        // 反射获取泛型父类类型
        Type genericSuperclass = getClass().getGenericSuperclass();
        // 提取泛型参数 T 的真实类型
        // noinspection DataFlowIssue
        type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return type;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getDataClass() {
        // Type genericSuperclass = getClass().getGenericSuperclass();
        // if (genericSuperclass instanceof ParameterizedType) {
        //     ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        //     Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        //     //noinspection ConstantValue
        //     if (actualTypeArguments != null && actualTypeArguments.length > 0) {
        //         return (Class<T>) actualTypeArguments[0];
        //     }
        // }
        // return null;

        Type dataType = getType();
        if (dataType == null) {
            return null;
        }

        // 如果是具体的 Class 类型
        if (dataType instanceof Class) {
            return (Class<T>) dataType;
        }

        // 如果是参数化类型（如 TestN<User1, User2>），返回原始类型 TestN
        if (dataType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) dataType).getRawType();
            if (rawType instanceof Class) {
                return (Class<T>) rawType;
            }
        }

        // 如果是类型变量，无法转换为 Class
        throw new IllegalStateException(
                "Cannot cast type to Class. Actual type: " + dataType.getClass().getName() +
                        ", Type info: " + dataType
        );
    }
}
