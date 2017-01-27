package hu.bme.mit.codemodel.rifle.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Nil;
import com.shapesecurity.shift.ast.SourceSpan;
import com.shapesecurity.shift.parser.ParserWithLocation;
import com.shapesecurity.shift.scope.Scope;

/**
 * Created by steindani on 2/26/16.
 */
public class GraphIterator {

    protected final String path;
    protected final ParserWithLocation parserWithLocation;
    protected final DbServices dbServices;
    protected final IdentityHashMap<Object, Object> done = new IdentityHashMap<>();
    protected final Map<Object, org.neo4j.graphdb.Node> nodes = new IdentityHashMap<>();

    protected final BlockingQueue<QueueItem> queue = new LinkedBlockingQueue<>();

    public GraphIterator(DbServices dbServices, String path, ParserWithLocation parserWithLocation) {
        this.dbServices = dbServices;
        this.path = path;
        this.parserWithLocation = parserWithLocation;
    }

    public void iterate(Scope scope, String sessionId) {
        try (Transaction tx = dbServices.beginTx()) {

            createPathNode(sessionId, tx);
            queue.add(new QueueItem(null, null, scope));

            while (!queue.isEmpty()) {
                QueueItem queueItem = queue.take();
                process(queueItem, tx, sessionId);
            }

            tx.success();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createPathNode(String sessionId, Transaction tx) {
        storeType(tx, path, "CompilationUnit");
        storeProperty(tx, path, "path", path);
        storeProperty(tx, path, "session", sessionId);
    }

    protected void process(QueueItem queueItem, Transaction tx, String sessionId) {
        Object parent = queueItem.parent;
        String predicate = queueItem.predicate;
        Object node = queueItem.node;

        // TODO for presentation reasons the Nil node is hidden
        if (node == null || node instanceof Nil) {
            return;
        }

        if (isPrimitive(node.getClass())) {
            storeProperty(tx, parent, predicate, node);
        } else if (isCollection(node)) {
            handleCollection(tx, parent, predicate, node, sessionId);
        } else if (node instanceof Maybe || node instanceof Either) {
            handleFunctional(tx, parent, predicate, node, sessionId);
        } else if (node.getClass().getName().startsWith("com.shapesecurity")) {
            handleAstNode(tx, parent, predicate, node, sessionId);
        } else {
            System.err.println("WTF");
        }

    }

    protected static boolean isCollection(Object node) {
        return node instanceof ImmutableList
                || node instanceof Map
                || node instanceof HashTable
                || node instanceof ConcatList;
    }

    private void handleFunctional(Transaction tx, Object parent, String predicate, Object node, String sessionId) {
        if (done.containsKey(node)) {
            return;
        }
        done.put(node, node);

        if (node instanceof Maybe) {
            Maybe el = (Maybe) node;
            if (el.isJust()) {
                queue.add(new QueueItem(parent, predicate, el.just()));
//            } else {
//                queue.add(new QueueItem(node, fieldName, "null"));
            }
        } else if (node instanceof Either) {
            Either el = (Either) node;
            if (el.isLeft()) {
                handleFunctional(tx, parent, predicate, el.left(), sessionId);
            }
            if (el.isRight()) {
                handleFunctional(tx, parent, predicate, el.right(), sessionId);
            }
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

    protected void handleCollection(Transaction tx, Object parent, String predicate, Object node, String sessionId) {
        if (done.containsKey(node)) {
            return;
        }
        done.put(node, node);

        if (node instanceof ImmutableList || node instanceof ConcatList) {

            Iterable list = (Iterable) node;

            // id -- [field] -> list
            storeReference(tx, parent, predicate, list);
            storeType(tx, list, "List");
            storeReference(tx, path, "contains", list);
            handleIfInSession(tx, sessionId, list);

            final Iterator iterator = list.iterator();
            int i = 0;

            Object prev = null;

            while (iterator.hasNext()) {
                Object el = iterator.next();
                queue.add(new QueueItem(list, Integer.toString(i), el));

                if (prev != null) {
                    queue.add(new QueueItem(prev, "_next", el));
                }
                prev = el;

                // connect the children directly
                queue.add(new QueueItem(parent, predicate, el));

                i++;
            }

            storeReference(tx, list, "last", prev);
            createEndNode(tx, list, sessionId);

        } else if (node instanceof Map) {

            Map map = (Map) node;

            if (!map.isEmpty()) {
                // id -- [field] -> table
                storeReference(tx, parent, predicate, map);
//                storeType(tx, map, node.getClass().getSimpleName());
                storeType(tx, map, "Map");
                storeReference(tx, path, "contains", map);
                handleIfInSession(tx, sessionId, map);

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
                storeReference(tx, path, "contains", table);
                handleIfInSession(tx, sessionId, table);

                for (Object el : table.entries()) {
                    Pair pair = (Pair) el;
                    queue.add(new QueueItem(table, pair.a.toString(), pair.b));
                }
            }

        }
    }

    protected void handleAstNode(Transaction tx, Object parent, String predicate, Object node, String sessionId) {

        if (parent != null) {
            storeReference(tx, parent, predicate, node);
        }

        if (done.containsKey(node)) {
            return;
        }
        done.put(node, node);

        createEndNode(tx, node, sessionId);

        storeReference(tx, path, "contains", node);
        handleIfInSession(tx, sessionId, node);
        storeLocation(node);

        Class<?> nodeType = node.getClass();

        storeType(tx, node, nodeType.getSimpleName());
        // list superclasses, interfaces
        List<Class<?>> interfaces = Arrays.asList(nodeType.getInterfaces());
        interfaces.forEach(elem -> {
            final String interfaceName = elem.getSimpleName();
            storeType(tx, node, interfaceName);
            
            if (interfaceName.startsWith("Literal")) {
                storeType(tx, node, "Literal");
            }
        });

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
                        if (fieldType.isEnum()) {

                            // queue.add(new QueueItem(node, fieldName, o.toString()));
                            storeProperty(tx, node, fieldName, o.toString());

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

    protected void createEndNode(Transaction tx, Object node, String sessionId) {
        Object end = new Object();
        storeType(tx, end, "End");
        storeReference(tx, node, "_end", end);
        storeProperty(tx, node, "session", sessionId);
        storeReference(tx, path, "contains", end);
    }

    protected void storeLocation(Object node) {
        if (node instanceof com.shapesecurity.shift.ast.Node) {
            Maybe<SourceSpan> location = parserWithLocation.getLocation((com.shapesecurity.shift.ast.Node) node);
            if (location.isJust()) {
                queue.add(new QueueItem(node, "location", location.just()));
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
        if (subject == null || object == null) {
            return;
        }
        Node a = findOrCreate(tx, subject);
        Node b = findOrCreate(tx, object);
        a.createRelationshipTo(b, RelationshipType.withName(predicate));
    }

    public void storeProperty(Transaction tx, Object subject, String predicate, Object object) {
        if (object == null) {
            return;
        }
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
