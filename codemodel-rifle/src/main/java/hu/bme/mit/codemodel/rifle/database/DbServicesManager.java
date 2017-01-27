package hu.bme.mit.codemodel.rifle.database;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public class DbServicesManager {
    protected static Map<String, DbServices> dbServices = new HashMap<>();

    synchronized
    public static DbServices getDbServices(String branchId) {
        if (!dbServices.containsKey(branchId)) {
            final Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.none());
            final DbServices dbs = new DbServices(driver);
            dbServices.put(branchId, dbs);
        }

        return dbServices.get(branchId);
    }
}
