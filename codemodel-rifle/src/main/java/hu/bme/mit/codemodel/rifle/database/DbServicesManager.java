package hu.bme.mit.codemodel.rifle.database;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.Driver;

import neo4j.driver.testkit.Neo4jTestKitDriver;

public class DbServicesManager {
    protected static Map<String, DbServices> dbServices = new HashMap<>();

    synchronized public static DbServices getDbServices(String branchId) {
        if (!dbServices.containsKey(branchId)) {
            // final Driver driver = GraphDatabase.driver("bolt://localhost",
            // AuthTokens.none());
            // use our mock driver for testing
            final Driver driver = new Neo4jTestKitDriver();
            final DbServices dbs = new DbServices(driver);
            dbServices.put(branchId, dbs);
        }

        return dbServices.get(branchId);
    }
}