package com.rc.jdbc.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JDBCUtils
 * @Description 获取链接, 关闭资源
 * @Author liux
 * @Date 19-9-4 下午3:31
 * @Version 1.0
 */
public class JDBCUtils {

    public static Properties loadProperty(String fileName) throws IOException {
        InputStream inputStream = JDBCUtils.class.getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    public static Connection getConn() throws Exception {
        Properties properties = loadProperty("jdbc.properties");

        String driverClass = properties.getProperty("driverClass");
        String url = properties.getProperty("jdbc.url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        Class.forName(driverClass);
        return DriverManager.getConnection(url, user, password);
    }

    static ComboPooledDataSource cpds = null;
    static {
        cpds = new ComboPooledDataSource("intergalactoApp");
    }

    public static Connection getConnByC3p0() throws SQLException {
        return cpds.getConnection();
    }

    public static void release(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}