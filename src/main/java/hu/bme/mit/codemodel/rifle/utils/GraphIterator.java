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

    protected DbServices dbServices;
    protected Set<Object> done = new HashSet<>();
    protected Map<Object, org.neo4j.graphdb.Node> nodes = new HashMap<>();

    public GraphIterator(DbServices dbServices) {
        this.dbServices = dbServices;
    }

    public void iterate(Object parent, String predicate, Object node) {
        if (node == null) {
            return;
        }

        // TODO for presentation reasons the Nil node is hidden
        if (node instanceof Nil) {
            return;
        }

        if (node instanceof com.shapesecurity.shift.ast.Node) {
            Maybe<SourceSpan> location = com.shapesecurity.shift.parser.Parser.getLocation((com.shapesecurity.shift.ast.Node) node);
            if (location != null) {
                if (location.isJust()) {
                    iterate(node, "location", location.just());
                }
            }
        }


        if (node instanceof ImmutableList) {

            // connect the children directly
            ((ImmutableList) node).forEach(el -> iterate(parent, predicate, el));

        } else if (node instanceof Map) {

            Map map = (Map) node;

            if (!map.isEmpty()) {
                // id -- [field] -> table
                storeReference(parent, predicate, map);
                storeType(map, node.getClass().getSimpleName());
                storeType(map, "Map");

                for (Object el : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) el;
                    iterate(map, entry.getKey().toString(), entry.getValue());
                }
            }

        } else if (node instanceof HashTable) {

            HashTable table = (HashTable) node;

            if (table.length > 0) {
                // id -- [field] -> table
                storeReference(parent, predicate, table);
                storeType(table, "HashTable");

                for (Object el : table.entries()) {
                    Pair pair = (Pair) el;
                    iterate(table, pair.a.toString(), pair.b);
                }
            }

        } else if (node instanceof ConcatList) {

            // connect the children directlydbServices
            ((ConcatList) node).forEach(el -> iterate(node, predicate, el));

        } else {

            if (parent != null) {
                if (!isPrimitive(node.getClass()) || (node instanceof Iterable) || (node instanceof HashMap)) {
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


            Class<?> nodeType = node.getClass();

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
                            if (o instanceof Maybe) {

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

                            } else if (o instanceof Node) {

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

    protected Node findOrCreate(Object subject) {
        if (nodes.containsKey(subject)) {
            return nodes.get(subject);
        } else {
            Node node = dbServices.createNode(subject);
            nodes.put(subject, node);
            return node;
        }
    }

    public void storeReference(Object subject, String predicate, Object object) {
        try (final Transaction tx = dbServices.beginTx()) {
            Node node = findOrCreate(subject);
            Node other = findOrCreate(object);
            node.createRelationshipTo(other, RelationshipType.withName(predicate));

            tx.success();
        }
    }

    public void storeProperty(Object subject, String predicate, Object object) {
        try (final Transaction tx = dbServices.beginTx()) {
            Node node = findOrCreate(subject);
            node.setProperty(predicate, object);

            tx.success();
        }
    }

    public void storeType(Object subject, String type) {
        if (type == null || type.length() == 0) {
            return;
        }

        try (final Transaction tx = dbServices.beginTx()) {
            Node node = findOrCreate(subject);
            node.addLabel(Label.label(type));
            tx.success();
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
