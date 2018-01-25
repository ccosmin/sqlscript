# sqlscript
Sqlscript is a simple runner for  SQL scripts.

It supports SQL scripts where statements are separated by semi-colons ";". 

Sqlscript can send the scripts to execute to JDBC connection or a JPA EntityManager. So either you do JDBC or J2EE running in a container you are covered.

Sources for the scripts can be:
- a String
- an InputStream
- a file from resources

## Samples

Run a script from a string on top of a JDBC connection (HSQLDB).
```
Class.forName("org.hsqldb.jdbcDriver");
Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:sqlscript.db;shutdown=true");
String script = "create table x(col varchar(5000)); insert into x(col) values('interesting string');";

SQLScriptRunner runner = SQLScriptRunner.readFromString(script).withJdbcConnection(connection).execute();
```

Check the tests for the other samples.
