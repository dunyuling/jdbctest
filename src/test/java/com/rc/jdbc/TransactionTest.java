package com.rc.jdbc;

import com.rc.jdbc.dao.JDBCDao;
import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;

/**
 * @ClassName TransactionTest
 * @Description 事务基本操作测试类
 * @Author liux
 * @Date 19-9-5 下午1:09
 * @Version 1.0
 */
public class TransactionTest {

    //不能保证事务
    @org.junit.Test
    public void testTransaction() {
        String sql1 = "update user set balance = balance - 500 where id = 1";
        JDBCDao.update(sql1);

        int i = 10 / 0;

        String sql2 = "update user set balance = balance +  500 where id = 2";
        JDBCDao.update(sql2);
    }

    //能够保证事务
    //TODO 通过动态代理实现模板化
    @Test
    public void testTransaction2() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            System.out.println(connection.getTransactionIsolation());
            connection.setAutoCommit(false);

            String sql1 = "update user set balance = balance - 500 where id = 1";
            JDBCDao.update2(connection, sql1);

            int i = 10 / 0;

            String sql2 = "update user set balance = balance +  500 where id = 2";
            JDBCDao.update2(connection, sql2);

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }
}