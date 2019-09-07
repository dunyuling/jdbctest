package com.rc.jdbc;

import com.rc.jdbc.model.Student;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName BeanUtilsTest
 * @Description TODO
 * @Author liux
 * @Date 19-9-5 上午10:15
 * @Version 1.0
 */
public class BeanUtilsTest {

    @Test
    public void testSetProperty() throws InvocationTargetException, IllegalAccessException {
        Object obj = new Student();
        System.out.println(obj);

        BeanUtils.setProperty(obj,"name","xyz");
        System.out.println(obj);
    }

    @Test
    public void testGetProperty() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object obj = new Student();
        System.out.println(obj);

        BeanUtils.setProperty(obj,"id","100");
        System.out.println(obj);

        String id = BeanUtils.getProperty(obj,"id");
        System.out.println(id);
    }
}
