package com.rc.jdbc;

import com.rc.jdbc.dao.JDBCDao;
import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;

/**
 * @ClassName TransactionDirtyReadTest
 * @Description 事务脏读演示
 * @Author liux
 * @Date 19-9-5 下午1:49
 * @Version 1.0
 */
public class TransactionDirtyReadTest {

    //脏读演示
    //1.以debug方式运行 testDirtyRead1(暂停于断点处)
    //2.以run方式运行testDirtyRead2,便可读到testDirtyRead1尚未提交的数据,即脏数据
    @Test
    public void testDirtyRead1() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            connection.setAutoCommit(false);

            String sql1 = "update user set balance = balance - 500 where id = 1";
            JDBCDao.update2(connection, sql1);

            //下面一行添加断点
            System.out.println("---");
//            connection.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testDirtyRead2() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            connection.setAutoCommit(false);

            String sql = "select balance from user where id = ?";
            Object obj = JDBCDao.getAttrByOuterConn(connection, sql, 1);

            System.out.println("obj: " + obj);
//            connection.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }
}
