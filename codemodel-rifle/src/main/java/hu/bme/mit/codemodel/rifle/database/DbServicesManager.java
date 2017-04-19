package hu.bme.mit.codemodel.rifle.database;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;

import neo4j.driver.testkit.EmbeddedTestkitDriver;

/**
 * Provides a DbServices singleton instance for each branch.
 */
public class DbServicesManager {
    private static Map<String, DbServices> dbServices = new HashMap<>();

    synchronized public static DbServices getDbServices(String branchId) {
        if (! dbServices.containsKey(branchId)) {
            final Driver driver = new EmbeddedTestkitDriver();
            final DbServices dbs = new DbServices(driver);
            dbServices.put(branchId, dbs);
        }

        return dbServices.get(branchId);
    }
}
