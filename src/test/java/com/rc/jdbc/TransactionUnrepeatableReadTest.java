package com.rc.jdbc;

import com.rc.jdbc.dao.JDBCDao;
import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;

/**
 * @ClassName TransactionUnrepeatableReadTest
 * @Description 事务不可重复读测试类
 * @Author liux
 * @Date 19-9-5 下午1:49
 * @Version 1.0
 */
public class TransactionUnrepeatableReadTest {

    //==========================================
    //不可重复读演示
    //1.以debug方式运行 testUnrepeatableRead2(暂停于断点处),第一次读到X=a
    //2.以run方式运行 testUnrepeatableRead1, X = b, 提交事务
    //3.继续运行 testUnrepeatableRead2,便可读到 testUnrepeatableRead1 提交的数据 X=b.
    //  无法读取X=a.即为不可重复读
    @Test
    public void testUnrepeatableRead1() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);

            String sql1 = "update user set balance = balance - 500 where id = 1";
            JDBCDao.update2(connection, sql1);

            System.out.println("---");
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testUnrepeatableRead2() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);

            //第一次读取
            String sql = "select balance from user where id = ?";
            Object obj = JDBCDao.getAttrByOuterConn(connection, sql, 1);
            System.out.println("1: " + obj);

            //下面一行添加断点
            sql = "select balance from user where id = ?";
            obj = JDBCDao.getAttrByOuterConn(connection, sql, 1);
            System.out.println("2: " + obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }
}