package com.snowlinesoftware.sql;

import javax.persistence.EntityManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLScript implements Closeable {
    private final InputStream   inputStream;
    private       Connection    connection;
    private       EntityManager entityManager;
    private boolean             wrapInTransaction;

    private SQLScript(InputStream inputStream) {
        this.wrapInTransaction = false;
        if ( inputStream == null ) {
            throw new NullPointerException("Input stream must not be null");
        }
        this.inputStream = inputStream;
    }

    public static SQLScript fromResources(String resourceFileName) {
        return new SQLScript(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFileName));
    }

    public static SQLScript fromInputStream(InputStream inputStream) {
        return new SQLScript(inputStream);
    }

    public static SQLScript fromString(String str) {
        return new SQLScript(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
    }

    public SQLScript usingEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;

        return this;
    }

    public SQLScript usingJDBCURL(String jdbcUrl) throws SQLException {
        this.connection = DriverManager.getConnection(jdbcUrl);

        return this;
    }

    public SQLScript usingJDBCConnection(Connection connection) {
        this.connection = connection;

        return this;
    }

    public SQLScript wrapInTransaction(boolean explicitTransationBoundaries) {
        this.wrapInTransaction = explicitTransationBoundaries;

        return this;
    }

    public void execute() throws IOException, SQLException {
        if ( entityManager == null && connection == null ) {
            throw new NullPointerException("Call usingEntityManager or WithJdbcUrl");
        }

        if (wrapInTransaction) {
            if ( entityManager != null ) {
                entityManager.getTransaction().begin();
            }
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();
            int c;
            boolean quoteContext = false;
            while ((c = bufferedReader.read()) != -1) {
                sb.append((char)c);
                if ( c == '\'' ) {
                    quoteContext = ! quoteContext;
                }
                if (c == ';' && !quoteContext) {
                    String query = sb.toString();
                    if (query.isEmpty()) {
                        continue;
                    }
                    if (connection != null) {
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(query);
                        statement.close();
                    }
                    else {
                        entityManager.createNativeQuery(query).executeUpdate();
                    }
                    sb = new StringBuffer();
                }
            }
        } catch ( Exception e ) {
            if (connection != null) {
                connection.rollback();
            } else {
                if (wrapInTransaction) {
                    entityManager.getTransaction().rollback();
                }
            }

            throw e;
        }

        if ( connection != null ) {
            connection.commit();
        } else {
            if (wrapInTransaction) {
                entityManager.getTransaction().commit();
            }
        }
    }

    @Override public void close() {
        if ( inputStream != null ) {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                // Ignore this.
            }
        }
    }
}
