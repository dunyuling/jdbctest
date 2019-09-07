package com.rc.jdbc;

import com.rc.jdbc.utils.JDBCUtils;
import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

/**
 * @ClassName ProcedureTest
 * @Description TODO
 * @Author liux
 * @Date 19-9-7 下午2:53
 * @Version 1.0
 */
public class ProcedureTest {

    @Test
    public void testProcedure() {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = JDBCUtils.getConn();
            String sql = "{call pow2 (?,  ?, ?)}";
            callableStatement = connection.prepareCall(sql);

            callableStatement.setInt(1, 2);
            callableStatement.setInt(3, 3);


            callableStatement.registerOutParameter(2, Types.INTEGER);
            callableStatement.registerOutParameter(3, Types.INTEGER);

            callableStatement.execute();

            System.out.println("2^2: " + callableStatement.getInt(2)
                    + "\t 3^2:" + callableStatement.getInt(3));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, callableStatement, connection);
        }
    }

    @Test
    public void testFunction() {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = JDBCUtils.getConn();
            String sql = "{? = call pow3 (?)}";
            callableStatement = connection.prepareCall(sql);

            callableStatement.setInt(2, 2);
            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            System.out.println("2^3: " + callableStatement.getInt(1));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(null, callableStatement, connection);
        }
    }
}
