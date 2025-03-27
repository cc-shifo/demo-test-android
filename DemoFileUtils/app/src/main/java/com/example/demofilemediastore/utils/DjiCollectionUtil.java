package com.zhd.hiair.djisdkmanager;
/*
 *   WWWWWW||WWWWWW
 *    W W W||W W W
 *         ||
 *       ( OO )__________
 *        /  |           \
 *       /o o|    DJI     \
 *       \___/||_||__||_|| **
 *            || ||  || ||
 *           _||_|| _||_||
 *          (__|__|(__|__|
 *
 * Copyright (c) 2017, DJI All Rights Reserved.
 */



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:</p>
 *
 * @author create at 2018/8/23 下午4:49 by daniel for dji-pilot
 * @version v1.0
 */
public class DjiCollectionUtil {

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <S, R> List<R> transform(List<S> sourceList, Transformer<S, R> transformer) {
        List<R> list = new ArrayList<>();
        if (isNotEmpty(sourceList)) {
            for (S source : sourceList) {
                R target = transformer.transform(source);
                list.add(target);
            }
        }
        return list;
    }

    /**
     * api 版本问题，目前不能使用系统预置的Function接口，自己定义一个
     *
     * @param <S>
     * @param <R>
     */
    public interface Transformer<S, R> {
        R transform(S source);
    }



    /**
     * 转化成有序列表后是否相等
     * @param list1
     * @param list2
     * @param c
     * @param <T>
     * @return
     */
    public static <T> boolean isEquals(List<T> list1, List<T> list2,  Comparator<? super T> c){
        if (list1 == null && list2 == null) {
            return true;
        }
        //Only one of them is null
        else if(list1 == null || list2 == null) {
            return false;
        }
        else if(list1.size() != list2.size()) {
            return false;
        }

        //copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, c);
        Collections.sort(list2, c);

        return list1.equals(list2);
    }

    public static <T extends Comparable<T>> boolean isEquals(List<T> list1, List<T> list2){
        return isEquals(list1, list2, null);
    }
}
