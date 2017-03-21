package hu.bme.mit.codemodel.rifle.database;

import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

/**
 * Provides database services like transaction handling and query executing.
 */
public class DbServices {
    protected final Driver driver;
    protected Transaction transaction;

    public DbServices(Driver driver) {
        this.driver = driver;
    }

    public Transaction beginTx() {
        Session session = driver.session();
        this.transaction = session.beginTransaction();
        return this.transaction;
    }

    public StatementResult execute(String query) {
        return this.transaction.run(query);
    }

    public StatementResult execute(String query, Map<String, Object> parameters) {
        return this.transaction.run(query, parameters);
    }
}
