package hu.bme.mit.codemodel.rifle.database;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class DbServices {

    GraphDatabaseService gds = new GraphDatabaseFactory().newEmbeddedDatabase(new File("neo4j-db/data/"));

    public Result execute(String format) {
        return execute(format, Collections.emptyMap());
    }

    public Result execute(String format, Map<String, Object> parameters) {
        return gds.execute(format, parameters);
    }

    public Iterable<Node> getAllNodes() {
        return gds.getAllNodes();
    }

    public Node getNodeById(long id) {
        return gds.getNodeById(id);
    }

    public Transaction beginTx() {
        return gds.beginTx();
    }

    public GraphDatabaseService getGraphDb() {
        return gds;
    }

}
