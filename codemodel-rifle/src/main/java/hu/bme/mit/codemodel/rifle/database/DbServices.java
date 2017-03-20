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
    protected Session session;

    public DbServices(Driver driver) {
        this.driver = driver;
        this.session = this.driver.session();
    }

    public Transaction beginTx() {
        return this.session.beginTransaction();
    }

    public StatementResult execute(String query) {
        return this.session.run(query);
    }

    public StatementResult execute(String query, Map<String, Object> parameters) {
        return this.session.run(query, parameters);
    }
}
