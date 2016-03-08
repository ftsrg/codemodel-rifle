package hu.bme.mit.codemodel.rifle.utils;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.SourceSpan;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by steindani on 2/26/16.
 */
public class GraphIterator {

    private final Map<com.shapesecurity.shift.ast.Node, Maybe<SourceSpan>> locations;
    protected DbServices dbServices;
    protected Set<Object> done = new HashSet<>();
    protected Map<Object, org.neo4j.graphdb.Node> nodes = new HashMap<>();

    public GraphIterator(DbServices dbServices, Map<com.shapesecurity.shift.ast.Node, Maybe<SourceSpan>> locations) {
        this.dbServices = dbServices;
        this.locations = locations;
    }

    public void iterate(Transaction transaction, Object parent, String predicate, Object node) {
        final Transaction tx = (transaction == null) ? dbServices.beginTx() : transaction;

        if (node == null) {
            return;
        }

        // TODO for presentation reasons the Nil node is hidden
        if (node instanceof Nil) {
            return;
        }

        if (node instanceof com.shapesecurity.shift.ast.Node) {
            Maybe<SourceSpan> location = locations.get(node);
            if (location != null) {
                if (location.isJust()) {
                    iterate(tx, node, "location", location.just());
                }
            }
        }


        if (node instanceof ImmutableList) {

            // connect the children directly
            ((ImmutableList) node).forEach(el -> iterate(tx, parent, predicate, el));

        } else if (node instanceof Map) {

            Map map = (Map) node;

            if (!map.isEmpty()) {
                // id -- [field] -> table
                storeReference(tx, parent, predicate, map);
                storeType(tx, map, node.getClass().getSimpleName());
                storeType(tx, map, "Map");

                for (Object el : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) el;
                    iterate(tx, map, entry.getKey().toString(), entry.getValue());
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
                    iterate(tx, table, pair.a.toString(), pair.b);
                }
            }

        } else if (node instanceof ConcatList) {

            // connect the children directlydbServices
            ((ConcatList) node).forEach(el -> iterate(tx, node, predicate, el));

        } else {

            if (parent != null) {
                if (!isPrimitive(node.getClass()) || (node instanceof Iterable) || (node instanceof HashMap)) {
                    storeReference(tx, parent, predicate, node);
                } else {
                    storeProperty(tx, parent, predicate, node);
                    return;
                }
            }


            if (done.contains(node)) {
                return;
            }
            done.add(node);


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
                                    iterate(tx, node, fieldName, el.just());
                                } else {
                                    iterate(tx, node, fieldName, "null");
                                }

                            } else if (o instanceof Either) {
                                // TODO
                                iterate(tx, node, fieldName, o);

                            } else if (fieldType.isEnum()) {

                                iterate(tx, node, fieldName, o.toString());

                            } else if (o instanceof Node) {

                                iterate(tx, node, fieldName, o);

                            } else if (fieldType.getName().startsWith("com.shapesecurity.functional")) {

                                // TODO
                                iterate(tx, node, fieldName, o);

                            } else {

                                // TODO
                                iterate(tx, node, fieldName, o);

                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }

        if (transaction == null) {
            tx.success();
            tx.close();
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

}
