package com.rc.jdbc.dao;

import com.rc.jdbc.utils.JDBCUtils;
import com.rc.jdbc.utils.ReflectionUtils;
import org.apache.commons.beanutils.BeanUtils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JDBCDao
 * @Description TODO
 * @Author liux
 * @Date 19-9-6 下午12:18
 * @Version 1.0
 */
public class JDBCDao {
    /*
     * @Author liux
     * @Description 通过ResultSetMetaData和反射为对象赋值
     * @Date 19-9-4 下午7:06
     * @param clazz: 描述对象类型
     * @param sql: SQL语句,可能带有占位符
     * @param args: 填充占位符的可变参数
     * @return T
     **/
    public static <T> T getEntityByReflect(Class<T> clazz, String sql, Object... args) {
        T entity = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            Map<String, Object> values = new HashMap<>();

            if (resultSet.next()) {
                //通过ResultSetMetaData获取对象对应属性名和属性值
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i + 1);
                    String columnValue = resultSet.getString(columnLabel);
                    values.put(columnLabel, columnValue);
                }

                //通过反射为对象赋值
                entity = clazz.newInstance();
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    ReflectionUtils.setFieldValue(entity, entry.getKey(), entry.getValue());
                }
                System.out.println("entity: " + entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
        return entity;
    }

    /*
     * @Author liux
     * @Description 1.通过ResultSetMetaData和反射为对象赋值
     *              2.为了验证事务幻读,使用外部传入连接
     * @Date 19-9-5 下午2:03
     * @param clazz
     * @param sql
     * @param args
     * @return T
     **/
    public static <T> List<T> getEntityByReflectAndOuterConn(Connection connection, Class<T> clazz, String sql, Object... args) {
        List<T> list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {
                //通过ResultSetMetaData获取对象对应属性名和属性值
                //通过反射为对象赋值
                T entity = clazz.newInstance();
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    String columnLabel = resultSetMetaData.getColumnLabel(i + 1);
                    String columnValue = resultSet.getString(columnLabel);
//                    ReflectionUtils.setFieldValue(entity, columnLabel, columnValue);
                    //已有工具,效果会更好
                    BeanUtils.setProperty(entity, columnLabel, columnValue);
                }
                list.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, null);
        }
        return list;
    }

    public static <E> E getAttr(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {

                return (E) resultSet.getObject(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /*
     * @Author liux
     * @Description 为了验证事务的隔离特性,连接对象从外部传入
     * @Date 19-9-5 下午1:27
     * @param connection
     * @param sql
     * @param args
     * @return E
     **/
    public static <E> E getAttrByOuterConn(Connection connection, String sql, Object... args) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {

                return (E) resultSet.getObject(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, null);
        }
        return null;
    }

    public static <E> E getAutoGeneratedPrimaryKey(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            preparedStatement.executeUpdate();


            resultSet = preparedStatement.getGeneratedKeys();

            //GENERATED_KEY
            /*ResultSetMetaData rsmd = resultSet.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                System.out.println(rsmd.getColumnLabel(i+1));
            }*/

            if (resultSet.next()) {
                return (E) resultSet.getObject(1);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
        return null;
    }


    //插入包含blob列的数据
    public static void insertBlob(String sql, String path) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBlob(1, new FileInputStream(new File(path)));
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
    }

    //查询blob列数据的内容
    public static byte[] getBlob(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBytes(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
        return null;
    }

    public static void update(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConn();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, connection);
        }
    }

    /*
     * @Author liux
     * @Description 1.为了保证事务,使用外部Connection
     *              2.不在该方法释放连接
     * @Date 19-9-5 下午1:24
     * @param connection
     * @param sql
     * @param args
     * @return void
     **/
    public static void update2(Connection connection, String sql, Object... args) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(resultSet, preparedStatement, null);
        }
    }
}
