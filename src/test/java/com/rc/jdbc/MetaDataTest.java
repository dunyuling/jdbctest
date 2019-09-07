package com.rc.jdbc;

import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.*;

/**
 * @ClassName MetaDataTest
 * @Description TODO
 * @Author liux
 * @Date 19-9-5 上午10:51
 * @Version 1.0
 */
public class MetaDataTest {


    @Test
    public void testDatabaseMetaData() {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            System.out.println("版本：　" + databaseMetaData.getDriverMajorVersion() + "." + databaseMetaData.getDatabaseMinorVersion());

            System.out.println("用户: " + databaseMetaData.getUserName());

            System.out.print("数据库: ");
            resultSet = databaseMetaData.getCatalogs();
            while (resultSet.next()) {
                System.out.print(resultSet.getObject(1) + "\t");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, null, connection);
        }
    }

    @Test
    public void testResultSetMetaData() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            String sql = "select name as n,password as p from user";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData rsmd = resultSet.getMetaData();

            System.out.println("列数: " + rsmd.getColumnCount());
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                System.out.println("列名: " + rsmd.getColumnName(i + 1) + ",\t别名: " + rsmd.getColumnLabel(i + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
    }
}