/**
 * Created by steindani on 1/19/16.
 */

package hu.bme.mit.codemodel.rifle;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;
import scala.App;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class Application {


    final String DB_PATH = "/home/steindani/Downloads/neo4j-community-3.0.0-M02/data/graph.db";
    final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);


    protected ArrayList<Object> done = new ArrayList<>();
    protected Map<Object, org.neo4j.graphdb.Node> nodes = new HashMap<>();

    public static void main(String[] args) throws JsError {
        String source = "function x() {\n" +
                "  return 2;\n" +
                "}\n" +
                "\n" +
                "function y() {\n" +
                "  return 3;\n" +
                "}\n" +
                "\n" +
                "function z() {\n" +
                "  x();\n" +
                "  y();\n" +
                "}\n" +
                "\n" +
                "z();";
        Script program = Parser.parseScript(source);

        GlobalScope global = ScopeAnalyzer.analyze(program);

        Application application = new Application();
        application.iterate(null, null, global);
        application.export();

    }

    public Application() {

        try (Transaction transaction = graphDb.beginTx()) {
            graphDb.getAllNodes().forEach(node -> {
                node.getRelationships().forEach(
                        relationship -> relationship.delete()
                );
                node.delete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export() {
        try (Transaction transaction = graphDb.beginTx()) {
            GraphvizWriter writer = new GraphvizWriter();
            writer.emit(System.out, Walker.fullGraph(graphDb));
            transaction.success();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iterate(Object parent, String predicate, Object node) {
        if (node == null) {
            return;
        }

        Class<?> nodeType = node.getClass();

        if (parent != null) {
            if (node.getClass().getName().startsWith("com.shapesecurity") || (node instanceof Iterable) || (node instanceof HashMap)) {
                storeReference(parent, predicate, node);
            } else {
                storeProperty(parent, predicate, node);
                return;
            }
        }


        if (done.contains(node)) {
            return;
        }
        done.add(node);


        storeType(node, nodeType.getSimpleName());
        // list superclasses, interfaces
        List<Class<?>> interfaces = Arrays.asList(nodeType.getInterfaces());
        interfaces.forEach(elem -> storeType(node, elem.getSimpleName()));

        Class<?> superclass = nodeType.getSuperclass();
        while (superclass != Object.class) {
            storeType(node, superclass.getSimpleName());
            superclass = superclass.getSuperclass();
        }

        getAllFields(nodeType).forEach(
                field -> {
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    String fieldName = field.getName();


                    try {
                        Object o = field.get(node);
                        if (o instanceof ImmutableList) {

                            // connect the children directly
                            ((ImmutableList) o).forEach(el -> iterate(node, fieldName, el));

                        } else if (o instanceof Map) {

                            Map map = (Map) o;

                            // id -- [field] -> table
                            storeReference(node, fieldName, map);

                            for (Object el : map.entrySet()) {
                                Map.Entry entry = (Map.Entry) el;
                                iterate(map, entry.getKey().toString(), entry.getValue());
                            }


                        } else if (o instanceof HashTable) {

                            HashTable table = (HashTable) o;

                            // id -- [field] -> table
                            storeReference(node, fieldName, table);

                            for (Object el : table.entries()) {
                                Pair pair = (Pair) el;
                                iterate(table, pair.a.toString(), pair.b);
                            }

                        } else if (o instanceof ConcatList) {

                            // connect the children directly
                            ((ConcatList) o).forEach(el -> iterate(node, fieldName, el));

                        } else if (o instanceof Maybe) {

                            Maybe el = (Maybe) o;
                            if (el.isJust()) {
                                iterate(node, fieldName, el.just());
                            } else {
                                iterate(node, fieldName, "null");
                            }

                        } else if (o instanceof Either) {
                            // TODO
                            iterate(node, fieldName, o);

                        } else if (fieldType.isEnum()) {

                            iterate(node, fieldName, o.toString());

                        } else if (fieldType.getName().startsWith("com.shapesecurity.shift.ast")) {

                            iterate(node, fieldName, o);

                        } else if (fieldType.getName().startsWith("com.shapesecurity.functional")) {

                            // TODO
                            iterate(node, fieldName, o);

                        } else {

                            // TODO
                            iterate(node, fieldName, o);

                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    protected void storeReference(Object subject, String predicate, Object object) {
        try (final Transaction tx = graphDb.beginTx()) {
            org.neo4j.graphdb.Node node = findOrCreate(subject);
            org.neo4j.graphdb.Node other = findOrCreate(object);
            node.createRelationshipTo(other, RelationshipType.withName(predicate));

            tx.success();
        }
    }

    protected void storeProperty(Object subject, String predicate, Object object) {
        try (final Transaction tx = graphDb.beginTx()) {
            org.neo4j.graphdb.Node node = findOrCreate(subject);
            node.setProperty(predicate, object);

            tx.success();
        }
    }

    protected void storeType(Object subject, String type) {
        try (final Transaction tx = graphDb.beginTx()) {
            org.neo4j.graphdb.Node node = findOrCreate(subject);
            node.addLabel(Label.label(type));
            tx.success();
        }
    }

    protected org.neo4j.graphdb.Node findOrCreate(Object subject) {
        if (nodes.containsKey(subject)) {
            return nodes.get(subject);
        } else {
            try (final Transaction tx = graphDb.beginTx()) {
                org.neo4j.graphdb.Node node = graphDb.createNode();
                tx.success();

                nodes.put(subject, node);

                return node;
            }
        }

    }

    // http://stackoverflow.com/questions/3567372/access-to-private-inherited-fields-via-reflection-in-java
    protected List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }

        return fields;
    }

}
