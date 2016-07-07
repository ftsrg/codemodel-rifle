package hu.bme.mit.codemodel.rifle.utils;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steindani on 3/13/16.
 */
public class DbServicesManager {
    protected static Map<String, DbServices> dbServices = new HashMap<>();

    synchronized
    public static DbServices getDbServices(String branchId) {
        if (!dbServices.containsKey(branchId)) {
            final String DB_PATH = "database" + File.separator + branchId;

            final GraphDatabaseFactory graphDatabaseFactory = new GraphDatabaseFactory();
            final GraphDatabaseBuilder graphDatabaseBuilder = graphDatabaseFactory.newEmbeddedDatabaseBuilder(new File(DB_PATH));

//            graphDatabaseBuilder
//                    .setConfig(GraphDatabaseSettings.node_keys_indexable, "true")
//                    .setConfig(GraphDatabaseSettings.relationship_keys_indexable, "true")
//                    .setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
//                    .setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true");

            final GraphDatabaseService graphDb = graphDatabaseBuilder.newGraphDatabase();
            final DbServices dbs = new DbServices(graphDb);

            dbServices.put(branchId, dbs);
        }

        return dbServices.get(branchId);
    }
}
