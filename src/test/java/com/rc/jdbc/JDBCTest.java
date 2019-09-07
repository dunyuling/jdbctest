package com.rc.jdbc;

import com.rc.jdbc.dao.JDBCDao;
import com.rc.jdbc.model.Student;
import com.rc.jdbc.model.User;
import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JDBCTest
 * @Description TODO
 * @Author liux
 * @Date 19-9-4 上午11:01
 * @Version 1.0
 */
public class JDBCTest {

    //原始方式
    @Test
    public void testDriver() throws SQLException {
        Driver driver = new com.mysql.cj.jdbc.Driver();

        String url = "jdbc:mysql://localhost:3306/test";
        Properties properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "mysql");

        Connection connection = driver.connect(url, properties);
        System.out.println(connection);
    }

    //通用方式结合配置文件
    public Connection getConn1() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        String driverClass = properties.getProperty("driverClass");
        String url = properties.getProperty("jdbc.url");

        Driver driver = (Driver) Class.forName(driverClass).newInstance();
        Connection connection = driver.connect(url, properties);
        System.out.println(connection);
        return connection;
    }

    //通用方式结合配置文件
    public Connection getConn2() throws IOException, ClassNotFoundException, SQLException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        String driverClass = properties.getProperty("driverClass");
        String url = properties.getProperty("jdbc.url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        Class.forName(driverClass);
        Class.forName("org.postgresql.Driver");
        //可以注册多个连接驱动
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
        return DriverManager.getConnection(url, user, password);
    }

    @Test
    public void testGetConn() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, IOException {
        System.out.println(getConn2());
    }

    @Test
    public void testStatement() {
        Connection connection = null;
        Statement statement = null;
        String sql = "insert into tt values()";

        try {
            connection = JDBCUtils.getConn();
            statement = connection.createStatement();
            int state = statement.executeUpdate(sql);
            System.out.println("state: " + state);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, statement, connection);
        }
    }

    @Test
    public void testResultSet() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String sql = "select id from tt";

        try {
            connection = JDBCUtils.getConn();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            System.out.println(connection);
            System.out.println(statement);
            System.out.println(resultSet);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                System.out.println("id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, statement, connection);
        }
    }

    //sql注入
    @Test
    public void testStatement2() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String name = "a' or password=";
        String password = " or '1'='1";
        String sql = "select name,password from user where name = '" + name
                + "' and password ='" + password + "'";

        System.out.println(sql);

        try {
            connection = JDBCUtils.getConn();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                System.out.println("name: " + resultSet.getString("name") +
                        "\tpassword: " + resultSet.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, statement, connection);
        }
    }

    //防止sql注入
    @Test
    public void testPrepareStatement() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "select name,password from user where name = ? and password=?";

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "a' or password=");
            preparedStatement.setString(2, " or '1'='1");


            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("name: " + resultSet.getString("name") +
                        "password: " + resultSet.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
    }

    //通过ResultSetMetaData和反射为对象赋值
    @Test
    public void testGetEntityByReflect() {
        String sql = "select name,password from teacher";
        User user = JDBCDao.getEntityByReflect(User.class, sql);
        System.out.println("user: " + user);

        System.out.println("--------");
        sql = "select name,password from student";
        Student student = JDBCDao.getEntityByReflect(Student.class, sql);
        System.out.println("student: " + student);
    }

    //获取对象属性
    @Test
    public void testGetAttr() {
        String sql = "select password from teacher where name = ?";
        Object obj = JDBCDao.getAttr(sql, "xyz");
        System.out.println("obj: " + obj);

        System.out.println("--------");
    }

    @Test
    public void testGetAutoGeneratedPrimaryKey() {
        String sql = "insert into tt values()";
        Object obj = JDBCDao.getAutoGeneratedPrimaryKey(sql);
        System.out.println(obj);
    }

    @Test
    public void testInsertBlob() {
        String sql = "insert into picture values(null,?)";
        String path = "/home/liux/图片/blob.png";
        JDBCDao.insertBlob(sql, path);
    }

    @Test
    public void readBlob() {
        String sql = "select content from picture where id = ?";
        int id = 2;
        byte[] buff = JDBCDao.getBlob(sql, id);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File("/home/liux/test/" + id + ".png"));
            fileOutputStream.write(buff);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}