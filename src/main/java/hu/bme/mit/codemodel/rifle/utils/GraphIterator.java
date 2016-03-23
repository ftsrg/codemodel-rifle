package hu.bme.mit.codemodel.rifle.utils;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.SourceSpan;
import com.shapesecurity.shift.scope.Scope;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by steindani on 2/26/16.
 */
public class GraphIterator {

    private final Map<com.shapesecurity.shift.ast.Node, Maybe<SourceSpan>> locations;
    protected DbServices dbServices;
    protected Set<Object> done = new HashSet<>();
    protected Map<Object, org.neo4j.graphdb.Node> nodes = new HashMap<>();

    protected BlockingQueue<QueueItem> queue = new LinkedBlockingQueue<>();

    public GraphIterator(DbServices dbServices, Map<com.shapesecurity.shift.ast.Node, Maybe<SourceSpan>> locations) {
        this.dbServices = dbServices;
        this.locations = locations;
    }

    public void iterate(Scope scope, String sessionId) {
        try (Transaction tx = dbServices.beginTx()) {

            queue.add(new QueueItem(null, null, scope));

            while (!queue.isEmpty()) {
                System.out.println(queue.size());
                final QueueItem queueItem = queue.take();
                process(queueItem, tx, sessionId);
            }

            tx.success();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void process(QueueItem queueItem, Transaction tx, String sessionId) {
        Object parent = queueItem.parent;
        String predicate = queueItem.predicate;
        Object node = queueItem.node;

        // TODO for presentation reasons the Nil node is hidden
        if (node == null || node instanceof Nil) {
            return;
        }

        // If the sessionId is set, mark the node temporal and store the id.
        handleIfInSession(tx, sessionId, node);

        if (node instanceof ImmutableList
                || node instanceof Map
                || node instanceof HashTable
                || node instanceof ConcatList
                ) {
            if (done.contains(node)) {
                return;
            }
            done.add(node);
            handleCollection(tx, parent, predicate, node);
        } else {

            if (parent != null) {
                if (isPrimitive(node.getClass())) {
                    storeProperty(tx, parent, predicate, node);
                } else {
                    storeReference(tx, parent, predicate, node);
                }
            }
            if (done.contains(node)) {
                return;
            }
            done.add(node);

            handleAstNode(tx, parent, predicate, node);
        }


    }

    /**
     * If the sessionId is set, mark the node temporal and store the id.
     * Since we are only iterating one AST, there is no way a Node from another graph is mislabeled.
     *
     * @param tx
     * @param sessionId
     * @param node
     */
    protected void handleIfInSession(Transaction tx, String sessionId, Object node) {
        if (sessionId != null) {
            storeType(tx, node, "Temp");
            storeProperty(tx, node, "session", sessionId);
        }
    }

    protected void handleCollection(Transaction tx, Object parent, String predicate, Object node) {
        if (node instanceof ImmutableList) {

            // connect the children directly
            ((ImmutableList) node).forEach(el -> queue.add(new QueueItem(parent, predicate, el)));

        } else if (node instanceof Map) {

            Map map = (Map) node;

            if (!map.isEmpty()) {
                // id -- [field] -> table
                storeReference(tx, parent, predicate, map);
                storeType(tx, map, node.getClass().getSimpleName());
                storeType(tx, map, "Map");

                for (Object el : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) el;
                    queue.add(new QueueItem(map, entry.getKey().toString(), entry.getValue()));
                }
            }

        } else if (node instanceof HashTable) {

            HashTable table = (HashTable) node;

            if (table.length > 0) {
                // id -- [field] -> table
                storeReference(tx, parent, predicate, table);
                storeType(tx, table, "HashTable");

                for (Object el : table.entries()) {
                    Pair pair = (Pair) el;
                    queue.add(new QueueItem(table, pair.a.toString(), pair.b));
                }
            }

        } else if (node instanceof ConcatList) {

            // connect the children directlydbServices
            ((ConcatList) node).forEach(el -> queue.add(new QueueItem(node, predicate, el)));

        }
    }

    protected void handleAstNode(Transaction tx, Object parent, String predicate, Object node) {

        storeLocation(node);

        Class<?> nodeType = node.getClass();

        storeType(tx, node, nodeType.getSimpleName());
        // list superclasses, interfaces
        List<Class<?>> interfaces = Arrays.asList(nodeType.getInterfaces());
        interfaces.forEach(elem -> storeType(tx, node, elem.getSimpleName()));

        Class<?> superclass = nodeType.getSuperclass();
        while (superclass != Object.class) {
            storeType(tx, node, superclass.getSimpleName());
            superclass = superclass.getSuperclass();
        }


        getAllFields(nodeType).forEach(
                field -> {
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    String fieldName = field.getName();


                    try {
                        Object o = field.get(node);
                        if (o instanceof Maybe) {

                            Maybe el = (Maybe) o;
                            if (el.isJust()) {
                                queue.add(new QueueItem(node, fieldName, el.just()));
                            } else {
                                queue.add(new QueueItem(node, fieldName, "null"));
                            }

                        } else if (o instanceof Either) {
                            // TODO
                            queue.add(new QueueItem(node, fieldName, o));

                        } else if (fieldType.isEnum()) {

                            queue.add(new QueueItem(node, fieldName, o.toString()));

                        } else if (o instanceof Node) {

                            queue.add(new QueueItem(node, fieldName, o));

                        } else if (fieldType.getName().startsWith("com.shapesecurity.functional")) {

                            // TODO
                            queue.add(new QueueItem(node, fieldName, o));

                        } else {

                            // TODO
                            queue.add(new QueueItem(node, fieldName, o));

                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    protected void storeLocation(Object node) {
        if (node instanceof com.shapesecurity.shift.ast.Node) {
            Maybe<SourceSpan> location = locations.get((com.shapesecurity.shift.ast.Node) node);
            if (location != null) {
                if (location.isJust()) {
                    queue.add(new QueueItem(node, "location", location.just()));
                }
            }
        }
    }


    // http://stackoverflow.com/questions/209366/how-can-i-generically-tell-if-a-java-class-is-a-primitive-type
    public static boolean isPrimitive(Class c) {
        if (c.isPrimitive()) {
            return true;
        } else if (c == Byte.class
                || c == Short.class
                || c == Integer.class
                || c == Long.class
                || c == Float.class
                || c == Double.class
                || c == Boolean.class
                || c == Character.class) {
            return true;
        } else if (c == String.class) {
            return true;
        } else {
            return false;
        }
    }

    protected Node findOrCreate(Transaction tx, Object subject) {
        if (nodes.containsKey(subject)) {
            return nodes.get(subject);
        } else {
            Node node = dbServices.createNode(tx, subject);
            nodes.put(subject, node);
            return node;
        }
    }

    public void storeReference(Transaction tx, Object subject, String predicate, Object object) {
        Node node = findOrCreate(tx, subject);
        Node other = findOrCreate(tx, object);
        node.createRelationshipTo(other, RelationshipType.withName(predicate));
    }

    public void storeProperty(Transaction tx, Object subject, String predicate, Object object) {
        Node node = findOrCreate(tx, subject);
        node.setProperty(predicate, object);
    }

    public void storeType(Transaction tx, Object subject, String type) {
        if (type == null || type.length() == 0) {
            return;
        }

        Node node = findOrCreate(tx, subject);
        node.addLabel(Label.label(type));
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


    protected class QueueItem {
        public Object parent;
        public String predicate;
        public Object node;

        public QueueItem(Object parent, String predicate, Object node) {
            this.parent = parent;
            this.predicate = predicate;
            this.node = node;
        }
    }
}
