package hu.bme.mit.codemodel.rifle.database;

import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Node;

/**
 * Created by steindani on 2/26/16.
 */
public class DbServices {

    protected final Driver driver;

    public DbServices(Driver driver) {
        this.driver = driver;
    }

//    public void clean() {
//        try (Transaction transaction = beginTx()) {
//            graphDb.getAllNodes().forEach(node -> {
//                node.getRelationships().forEach(
//                        relationship -> relationship.delete()
//                );
//                node.delete();
//
//                transaction.success();
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void export(String path) {
//        FileOutputStream fileOutputStream = null;
//        try (Transaction transaction = beginTx()) {
//            fileOutputStream = new FileOutputStream(path);
//
//            GraphvizWriter writer = new GraphvizWriter();
//            writer.emit(fileOutputStream, Walker.fullGraph(graphDb));
//
//            transaction.success();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
    public Node createNode(Transaction tx, Object subject) {
        Node node = execute(String.format("CREATE (n) RETURN n")).single().get(0).asNode();
        return node;
    }

    Transaction transaction;

    public Transaction beginTx() {
        Session session = driver.session();
        transaction = session.beginTransaction();
        return transaction;
    }

    public StatementResult execute(String query) {
    	return transaction.run(query);
    }

    public StatementResult execute(String query, Map<String, Object> parameters) {
    	return transaction.run(query, parameters);
    }

	public Node getNodeById(long id) {
		return null;
	}

	public Iterable<Node> getAllNodes() {
		return null; //graphDb.getAllNodes();
	}

}
