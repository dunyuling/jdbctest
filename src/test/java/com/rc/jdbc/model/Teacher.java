package com.rc.jdbc.model;

/**
 * @ClassName Teacher
 * @Description
 * @Author liux
 * @Date 19-9-5 下午12:31
 * @Version 1.0
 */
public class Teacher {

    private String name;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
