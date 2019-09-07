package com.rc.jdbc.model;

/**
 * @ClassName User
 * @Description User 实体类,为了演示反射建立
 * @Author liux
 * @Date 19-9-4 下午5:45
 * @Version 1.0
 */
public class User {

    private int id;

    private String name;

    private String password;

    private int balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                '}';
    }
}
