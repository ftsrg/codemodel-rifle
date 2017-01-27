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

    synchronized
    public static DbServices getDbServices(String branchId) {
        if (!dbServices.containsKey(branchId)) {
//            graphDatabaseBuilder
//                    .setConfig(GraphDatabaseSettings.node_keys_indexable, "true")
//                    .setConfig(GraphDatabaseSettings.relationship_keys_indexable, "true")
//                    .setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
//                    .setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true");

            final Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "admin"));
            final DbServices dbs = new DbServices(driver);
            dbServices.put(branchId, dbs);
        }

        return dbServices.get(branchId);
    }
}
