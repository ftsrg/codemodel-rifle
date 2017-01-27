package hu.bme.mit.codemodel.rifle.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

/**
 * Created by steindani on 2/26/16.
 */
public class DbServices {

    protected final GraphDatabaseService graphDb;

    public DbServices(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public void clean() {
        try (Transaction transaction = beginTx()) {
            graphDb.getAllNodes().forEach(node -> {
                node.getRelationships().forEach(
                        relationship -> relationship.delete()
                );
                node.delete();

                transaction.success();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export(String path) {
        FileOutputStream fileOutputStream = null;
        try (Transaction transaction = beginTx()) {
            fileOutputStream = new FileOutputStream(path);

            GraphvizWriter writer = new GraphvizWriter();
            writer.emit(fileOutputStream, Walker.fullGraph(graphDb));

            transaction.success();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Node createNode(Transaction tx, Object subject) {
        org.neo4j.graphdb.Node node = graphDb.createNode();

        return node;
    }

    public Transaction beginTx() {
        return graphDb.beginTx();
    }

    public Result execute(String query) {
    	return graphDb.execute(query);
    }

    public Result execute(String query, Map<String, Object> parameters) {
    	return graphDb.execute(query, parameters);
    }

	public Node getNodeById(long id) {
		return graphDb.getNodeById(id);
	}

	public ResourceIterable<Node> getAllNodes() {
		return graphDb.getAllNodes();
	}

	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

}
