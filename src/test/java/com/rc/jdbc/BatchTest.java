package com.rc.jdbc;

import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.*;
import java.time.LocalDate;

/**
 * @ClassName BatchTest
 * @Description 批量插入测试
 * TODO oracle 中效果改进非常明显,testBatchByStatement-->testBatchByPrepareStatement-->testBatch.
 * mysql 中没有什么影响,
 * postgresql 批量插入变化明显
 * TODO mysql 如何弥补批处理的不足哪?
 * @Author liux
 * @Date 19-9-5 下午6:17
 * @Version 1.0
 */
public class BatchTest {

    int count = 1000000;

    //mysql: 37620毫秒
    //postgresql:  25095毫秒
    @Test
    public void testBatchByStatement() {
        Connection connection = null;
        Statement statement = null;
        String sql = null;

        try {
            connection = JDBCUtils.getConn();
            connection.setAutoCommit(false);
            statement = connection.createStatement();

            long begin = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                sql = "insert into customer values(" + (i + 1) + ",'name" + (i + 1) + "','" + LocalDate.now() + "')";
//                System.out.println(sql);
                statement.executeUpdate(sql);
            }
            long end = System.currentTimeMillis();
            System.out.println(count + "条数据用时: " + (end - begin) + "毫秒");
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("被回滚");
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, statement, connection);
        }
    }

    //mysql: 35348毫秒
    //postgresql: 20465毫秒
    @Test
    public void testBatchByPrepareStatement() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql = "insert into customer values(?,?,?)";

        try {
            connection = JDBCUtils.getConn();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);

            long begin = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                preparedStatement.setObject(1, i + 1);
                preparedStatement.setObject(2, "name" + (i + 1));
                preparedStatement.setObject(3, LocalDate.now());
                preparedStatement.executeUpdate();
            }
            long end = System.currentTimeMillis();
            System.out.println(count + "条数据用时: " + (end - begin) + "毫秒");
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("被回滚");
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, preparedStatement, connection);
        }
    }

    //mysql: 34270毫秒
    //postgresql: 2674毫秒
    @Test
    public void testBatch() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql = "insert into customer values(?,?,?)";

        try {
            connection = JDBCUtils.getConn();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);

            long begin = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                preparedStatement.setObject(1, i + 1);
                preparedStatement.setObject(2, "name" + (i + 1));
                preparedStatement.setObject(3, LocalDate.now());
                preparedStatement.addBatch();

                if (count % 300 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }

            if (count % 300 != 0) {
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            }
            long end = System.currentTimeMillis();
            System.out.println(count + "条数据用时: " + (end - begin) + "毫秒");
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("被回滚");
            JDBCUtils.rollbackTransaction(connection);
        } finally {
            JDBCUtils.release(null, preparedStatement, connection);
        }
    }

    @Test
    public void testBatchByProcedure() {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String sql = "{ call batchInsert (?,?)}";

        try {
            connection = JDBCUtils.getConn();
            callableStatement = connection.prepareCall(sql);

            long begin = System.currentTimeMillis();

            callableStatement.setString(1, "lhg");
            callableStatement.setInt(2, 1);

            callableStatement.registerOutParameter(2, Types.VARCHAR);

            callableStatement.execute();


            long end = System.currentTimeMillis();
            System.out.println(count + "条数据用时: " + (end - begin) + "毫秒");
            System.out.println("count: " + callableStatement.getInt(2));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, callableStatement, connection);
        }
    }
}
