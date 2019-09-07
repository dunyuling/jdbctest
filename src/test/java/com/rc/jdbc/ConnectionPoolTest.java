package com.rc.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.rc.jdbc.utils.JDBCUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;
import sun.plugin.dom.exception.HierarchyRequestException;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @ClassName ConnectionPoolTest
 * @Description 数据库连接池测试类
 * @Author liux
 * @Date 19-9-5 下午7:41
 * @Version 1.0
 */
public class ConnectionPoolTest {

    @Test
    public void testDbcp() throws SQLException, IOException {
        BasicDataSource ds = new BasicDataSource();
        Properties properties = JDBCUtils.loadProperty("dbcp.properties");
        ds.setDriverClassName(properties.getProperty("driverClassName"));
        ds.setUrl(properties.getProperty("url"));
        ds.setUsername(properties.getProperty("username"));
        ds.setPassword(properties.getProperty("password"));

        System.out.println(ds.getMaxTotal());
        System.out.println(ds.getMinIdle());
        System.out.println(ds.getInitialSize());

        Connection connection = ds.getConnection();
        System.out.println(connection);
    }

    @Test
    public void testDBCPWithDataSourceFactory() throws Exception {
        Properties properties = JDBCUtils.loadProperty("dbcp.properties");
        BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(properties);

        System.out.println("maxTotal: " + dataSource.getMaxTotal() +
                "\t ,maxIdle: " + dataSource.getMaxIdle() +
                "\t ,minIdle: " + dataSource.getMinIdle() +
                "\t ,initialSize: " + dataSource.getInitialSize() +
                "\t ,maxWaitMillis: " + dataSource.getMaxWaitMillis());


        Connection connection1 = dataSource.getConnection();
        System.out.println("connection1: " + connection1);

        Connection connection2 = dataSource.getConnection();
        System.out.println("connection2: " + connection2);

        Connection connection3 = dataSource.getConnection();
        System.out.println("connection3: " + connection3);

        Connection connection4 = dataSource.getConnection();
        System.out.println("connection4: " + connection4);

        Connection connection5 = dataSource.getConnection();
        System.out.println("connection5: " + connection5);

        System.out.println("1: active num: " + dataSource.getNumActive());

        //超过最大连接时,必须要释放掉其中某些连接,才能让后来者正常获取
        new Thread(() -> {
            Connection connection6 = null;
            try {
                connection6 = dataSource.getConnection();
                System.out.println("connection6: " + connection6);
                System.out.println("3: active num: " + dataSource.getNumActive());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(5000);
        connection2.close();
        System.out.println("2: active num: " + dataSource.getNumActive());

        dataSource.close();
    }

    @Test
    public void testC3p0() throws PropertyVetoException, SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("org.postgresql.Driver"); //loads the jdbc driver
        cpds.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        cpds.setUser("postgres");
        cpds.setPassword("postgres");

// the settings below are optional -- c3p0 can work with defaults
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);

        Connection connection = cpds.getConnection();
        System.out.println(connection);
    }

    @Test
    public void testC3p0WithConfig() throws SQLException {
//        ComboPooledDataSource cpds = new ComboPooledDataSource("intergalactoApp");
//        Connection connection = cpds.getConnection();
//        System.out.println(connection);

        System.out.println(JDBCUtils.getConnByC3p0());
    }

    @Test
    public void testHikari() throws SQLException {
        HikariConfig config = new HikariConfig("src/test/resources/hikari.properties");
        HikariDataSource ds = new HikariDataSource(config);
        System.out.println(ds.getConnection());
    }

    @Test
    public void testDruid() throws Exception {
        Properties properties = JDBCUtils.loadProperty("druid.properties");

        //通过工厂
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        System.out.println(dataSource.getConnection());

        //
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.configFromPropety(properties);
        System.out.println(druidDataSource.getConnection());
    }
}