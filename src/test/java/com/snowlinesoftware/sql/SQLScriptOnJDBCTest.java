package com.snowlinesoftware.sql;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLScriptOnJDBCTest {
    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
    }

    @Test
    public void execute_on_jdbc_url() throws SQLException, IOException {
        SQLScript.fromString("create table x(col varchar(5000));")
                .withJdbcUrl("jdbc:hsqldb:mem:SQLScriptTestDB;shutdown=true")
                .execute();
    }

    @Test
    public void execute_on_jdbc_connection() throws SQLException, IOException {
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:SQLScriptTestDB;shutdown=true");

        SQLScript.fromString("SET DATABASE SQL SYNTAX ORA TRUE;")
                .withJDBCConnection(connection)
                .execute();
    }
}
