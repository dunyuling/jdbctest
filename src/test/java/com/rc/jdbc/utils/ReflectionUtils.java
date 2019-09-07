package com.rc.jdbc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName ReflectionUtils
 * @Description 使用反射的一些工具方法
 * @Author liux
 * @Date 19-9-4 下午6:02
 * @Version 1.0
 */
public class ReflectionUtils {

    /*
     * @Author liux
     * @Description 通过属性名和属性值构建对象
     * @Date 19-9-4 下午6:21
     * @param obj
     * @param fieldName
     * @param filedValue
     * @return void
     **/
    public static void setFieldValue(Object obj, String fieldName, Object filedValue) throws NoSuchFieldException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, filedValue);
    }

    /*
     * @Author liux
     * @Description 通过属性名和属性值构建对象
     *  TODO 需要知道属性值的类型,本例指定为String类型
     * @Date 19-9-4 下午6:33
     * @param obj
     * @param fieldName
     * @param filedValue
     * @return void
     **/
    public static void setFieldValue2(Object obj, String fieldName, Object filedValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String setMethodName = "set" + toUpperCaseFirstOne(fieldName);
        Method setReadOnly = obj.getClass().getMethod(setMethodName, String.class);
        setReadOnly.invoke(obj, filedValue);
    }

    private static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
