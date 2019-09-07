package com.rc.jdbc.dao;

import com.rc.jdbc.model.User;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @ClassName UserDao
 * @Description 获取继承父类的子类的泛型类型
 * @Author liux
 * @Date 19-9-7 下午12:43
 * @Version 1.0
 */
class BDao<T> {
}

class SDao<T> {

    private T t;

    SDao(T t) {
        this.t = t;
        System.out.println(t);
    }


}

public class UserDao extends BDao<User> {


    private Class<?> aClass = null;

    public UserDao() throws ClassNotFoundException {
        Type t = this.getClass().getGenericSuperclass();
        System.out.println("t: " + t);

        if (ParameterizedType.class.isAssignableFrom(t.getClass())) {
            for (Type t1 : ((ParameterizedType) t).getActualTypeArguments()) {
                aClass = Class.forName(t1.getTypeName());
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        UserDao userDao = new UserDao();
        System.out.println("aClass: " + userDao.aClass);

        new SDao<>(userDao.aClass);
    }
}