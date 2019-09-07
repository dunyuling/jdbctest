package com.rc.jdbc;

import com.rc.jdbc.dao.JDBCDao;
import com.rc.jdbc.model.User;
import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

/**
 * @ClassName TransactionPhantomReadTest
 * @Description 事务幻读测试类
 * @Author liux
 * @Date 19-9-5 下午1:56
 * @Version 1.0
 */
public class TransactionPhantomReadTest {

    //==========================================
    //幻读演示
    //1.以debug方式运行 testPhantomRead1(暂停于断点处),第一次读到list,其内容记为content1
    //2.以run方式运行 testPhantomRead2,插入或删除一些行.
    //3.继续运行 testPhantomRead1,第一次读到list,其内容记为content2.content1和content2 内容不同,即为幻读.
    //
    @Test
    public void testPhantomRead1() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);

            //第一次读取
            String sql = "select id,name,balance from user";
            List<User> list = JDBCDao.getEntityByReflectAndOuterConn(connection, User.class, sql);
            list.forEach(System.out::println);
            System.out.println("------------");


            //第一次读取,下面一行加断点
            sql = "update user set balance = 1000";
            JDBCDao.update2(connection,sql);

            sql = "select id,name,balance from user";
            list = JDBCDao.getEntityByReflectAndOuterConn(connection, User.class, sql);
            list.forEach(System.out::println);

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testPhantomRead2() {
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);

            String sql = "insert into user values(null,'d','dd',10001)";
//            String sql = "delete from user where id = 15";
            JDBCDao.update2(connection,sql);

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }
}