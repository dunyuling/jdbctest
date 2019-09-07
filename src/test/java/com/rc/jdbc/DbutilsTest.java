package com.rc.jdbc;

import com.rc.jdbc.model.User;
import com.rc.jdbc.utils.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DbutilsTest
 * @Description TODO
 * @Author liux
 * @Date 19-9-7 上午10:17
 * @Version 1.0
 */
public class DbutilsTest {

    @Test
    public void testQueryRunnerUpdate() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "delete from user where id in(?,?)";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            queryRunner.update(connection, sql, 14, 17);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    class MyResultSetHandler implements ResultSetHandler<User> {

        @Override
        public User handle(ResultSet rs) throws SQLException {
            User user = new User();
            if (rs.next()) {
                user.setId(rs.getInt(1));
                user.setName(rs.getString(2));
                user.setBalance(rs.getInt(3));
            }
            return user;
        }

        /*@Override
        public User handle(ResultSet rs) throws SQLException {
            System.out.println("handle ---");
            return "handler";
        }*/
    }

    @Test
    public void testQueryRunnerResultSetHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            User user = queryRunner.query(connection, sql, new MyResultSetHandler());
            System.out.println("user: " + user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testQueryRunnerBeanHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            User user2 = queryRunner.query(connection, sql, new BeanHandler<>(User.class));
            System.out.println("user2: " + user2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testQueryRunnerBeanListHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            List<User> userList = queryRunner.query(connection, sql, new BeanListHandler<>(User.class));
            System.out.println("userList: " + userList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testQueryRunnerMapHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            Map<String, Object> map = queryRunner.query(connection, sql, new MapHandler());
            System.out.println("map: " + map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testQueryRunnerBeanMapHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            Map<String, User> map2 = queryRunner.query(connection, sql, new BeanMapHandler<>(User.class));
            System.out.println("map2: " + map2.values());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testQueryRunnerMapListHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            List<Map<String, Object>> mapList = queryRunner.query(connection, sql, new MapListHandler());
            System.out.println("mapList: " + mapList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }

    @Test
    public void testQueryRunnerScalarHandler() {
        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name ,balance from user";
        Connection connection = null;

        try {
            connection = JDBCUtils.getConn();
            Object scalar = queryRunner.query(connection, sql, new ScalarHandler<>());
            System.out.println("scalar: " + scalar);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, null, connection);
        }
    }
}