package hu.bme.mit.codemodel.rifle.utils;

import org.neo4j.graphdb.*;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by steindani on 2/26/16.
 */
public class DbServices {

    protected GraphDatabaseService graphDb;

    public DbServices(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public void clean() {
        try (Transaction transaction = graphDb.beginTx()) {
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
        try (Transaction transaction = graphDb.beginTx()) {
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

    public Node createNode(Object subject) {
        try (final Transaction tx = graphDb.beginTx()) {
            org.neo4j.graphdb.Node node = graphDb.createNode();
            tx.success();

            return node;
        }
    }

    public Transaction beginTx() {
        return graphDb.beginTx();
    }
}
