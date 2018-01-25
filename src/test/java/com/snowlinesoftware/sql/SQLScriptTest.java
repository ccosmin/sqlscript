package com.snowlinesoftware.sql;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLScriptTest {
    private Connection connection;

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
    }

    @Before
    public void before() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:hsqldb:mem:SQLScriptTestDB;shutdown=true");

        Statement statement = this.connection.createStatement();
        statement.executeUpdate("drop schema public cascade");
        statement.executeUpdate("commit;");
    }

    @After
    public void after() throws SQLException {
        this.connection.close();
    }

    @Test
    public void execute_simple_create_table() throws IOException, SQLException {
        String script = "create table x(col varchar(5000));";

        SQLScript runner = SQLScript.fromString(script).withJDBCConnection(connection);

        runner.execute();
    }

    @Test
    public void execute_two_statements() throws IOException, SQLException {
        String script = "create table x(col varchar(5000)); insert into x(col) values('blah blah');";

        SQLScript runner = SQLScript.fromString(script).withJDBCConnection(connection);

        runner.execute();
    }

    @Test
    public void tolerate_semicolon_in_quote_context() throws IOException, SQLException {
        String script = "create table x(col varchar(5000)); insert into x(col) values('blah ; blah');";

        SQLScript runner = SQLScript.fromString(script).withJDBCConnection(connection);

        runner.execute();
    }

    @Test
    public void tolerate_escaped_single_quotes() throws IOException, SQLException {
        String script = "create table x(col varchar(5000)); insert into x(col) values('ff''');";

        SQLScript runner = SQLScript.fromString(script).withJDBCConnection(connection);

        runner.execute();

        Statement statement = connection.createStatement();
        try {
            ResultSet rs = statement.executeQuery("select col from x;");

            assertTrue(rs.next());
            assertEquals("ff'", rs.getString("col"));
        } finally {
            if ( statement != null ) {
                statement.close();
            }
        }
    }
}
