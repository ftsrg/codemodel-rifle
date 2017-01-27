package hu.bme.mit.codemodel.rifle.database;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class DbServices {

    public Result execute(String format) {
        // TODO Auto-generated method stub
        return null;
    }

    public Result execute(String format, Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterable<Node> getAllNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    public Node getNodeById(long rootId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Transaction beginTx() {
        // TODO Auto-generated method stub
        return null;
    }

    public GraphDatabaseService getGraphDb() {
        return null;
    }

}
