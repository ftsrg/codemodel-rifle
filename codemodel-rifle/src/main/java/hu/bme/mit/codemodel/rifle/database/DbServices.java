package hu.bme.mit.codemodel.rifle.database;

import java.util.Map;

import hu.bme.mit.codemodel.rifle.database.querybuilder.Query;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;

import neo4j.driver.testkit.EmbeddedTestkitDriver;

/**
 * Provides database services like transaction handling and query executing.
 */
public class DbServices {
    protected final Driver driver;
    protected Transaction transaction;

    public DbServices(Driver driver) {
        this.driver = driver;
    }

    public Driver getDriver() {
        return this.driver;
    }

    @Deprecated
    public Transaction beginTx() {
        Session session = driver.session();
        this.transaction = session.beginTransaction();
        return this.transaction;
    }

    @Deprecated
    public StatementResult execute(String statement) {
        return this.transaction.run(statement);
    }

    @Deprecated
    public StatementResult execute(String statementTemplate, Map<String, Object> statementParameters) {
        return this.transaction.run(statementTemplate, statementParameters);
    }

    @Deprecated
    public StatementResult execute(Query q) {
        return this.execute(q.getStatementTemplate(), q.getStatementParameters());
    }

    public GraphDatabaseService getUnderlyingDatabaseService() {
        if (driver instanceof EmbeddedTestkitDriver) {
            return ((EmbeddedTestkitDriver) driver).getUnderlyingDatabaseService();
        } else {
            throw new IllegalStateException("Cannot get underyling database service.");
        }
    }
}
