package hu.bme.mit.codemodel.rifle.database;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

/**
 * Created by steindani on 3/13/16.
 */
public class DbServicesManager {
    protected static Map<String, DbServices> dbServices = new HashMap<>();

    protected static final String USER = "neo4j";
    protected static final String PASSWORD = "neo4j";

    synchronized
    public static DbServices getDbServices(String branchId) {
        if (!dbServices.containsKey(branchId)) {
            final Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic(USER, PASSWORD));
            final DbServices dbs = new DbServices(driver);
            dbServices.put(branchId, dbs);
        }

        return dbServices.get(branchId);
    }
}
