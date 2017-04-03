package hu.bme.mit.codemodel.rifle.database;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import hu.bme.mit.codemodel.rifle.database.querybuilder.Query;
import hu.bme.mit.codemodel.rifle.database.querybuilder.QueryBuilder;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Node;

import com.google.common.base.Preconditions;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.SourceSpan;
import com.shapesecurity.shift.parser.ParserWithLocation;
import com.shapesecurity.shift.scope.Scope;

/**
 * After processing, the AST becomes an ASG.
 */
public class ASTScopeProcessor {
    /**
     * DbServices instance for branch.
     */
    protected final DbServices dbServices;

    /**
     * Path of the parsed file.
     */
    protected final String parsedFilePath;

    /**
     * A Shape's parser with location.
     */
    protected final ParserWithLocation parserWithLocation;

    /**
     * AST items that are already traversed and processed.
     */
    protected final Map<Object, Object> processedAstItems = new IdentityHashMap<>();

    /**
     * AST objects with ASG nodes.
     */
    protected final Map<Object, String> objectsWithAsgNodeIds = new HashMap<>();

    /**
     * The processing queue.
     */
    protected final BlockingQueue<QueueItem> processingQueue = new LinkedBlockingQueue<>();

    /**
     * QueryBuilder used during parsing a file.
     */
    protected final QueryBuilder queryBuilder = new QueryBuilder();

    /**
     * Internal class for processing.
     */
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

    /**
     * Default constructor.
     *
     * @param dbServices
     * @param parsedFilePath
     * @param parserWithLocation
     */
    public ASTScopeProcessor(DbServices dbServices, String parsedFilePath, ParserWithLocation parserWithLocation) {
        this.dbServices = dbServices;
        this.parsedFilePath = parsedFilePath;
        this.parserWithLocation = parserWithLocation;
    }

    /**
     * Processes a given scope with a sessionId.
     * <p>
     * The items (becoming ASG nodes) are stored in a blocking queue.
     *
     * @param scope
     * @param sessionId
     */
    public void processScope(Scope scope, String sessionId) {
        try (Transaction tx = dbServices.beginTx()) {
            this.createFilePathNode(sessionId);
            processingQueue.add(new QueueItem(null, null, scope));

            while (!processingQueue.isEmpty()) {
                QueueItem queueItem = processingQueue.take();
                process(queueItem, sessionId);
            }

            Query q = this.queryBuilder.getQuery();
            dbServices.execute(q.getStatementTemplate(), q.getStatementParameters());
            this.queryBuilder.clearBuilder();

            tx.success();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the file path node with the label CompilationUnit.
     *
     * @param sessionId
     */
    protected void createFilePathNode(String sessionId) {
        this.findOrCreate(parsedFilePath, new String[]{ "CompilationUnit" }, new String[]{ "parsedFilePath:" + parsedFilePath, "session:" + sessionId });

//        Query q = this.queryBuilder.getQuery();
//        this.dbServices.execute(q.getStatementTemplate(), q.getStatementParameters());
    }

    protected void process(QueueItem queueItem, String sessionId) {
        Object parent = queueItem.parent;
        String predicate = queueItem.predicate;
        Object node = queueItem.node;

        // TODO for presentation reasons the Nil node is hidden
        if (node == null) {
            return;
        }

        if (isPrimitive(node.getClass())) {
            storeProperty(parent, predicate, node);
        } else if (isCollection(node)) {
            handleCollection(parent, predicate, node, sessionId);
        } else if (node instanceof Maybe || node instanceof Either) {
            handleFunctional(parent, predicate, node, sessionId);
        } else if (node.getClass().getName().startsWith("com.shapesecurity")) {
            handleAstNode(parent, predicate, node, sessionId);
        } else {
            System.err.println("WTF");
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

    protected static boolean isCollection(Object node) {
        return node instanceof ImmutableList
            || node instanceof Map
            || node instanceof HashTable
            || node instanceof ConcatList;
    }

    protected void handleCollection(Object parent, String predicate, Object node, String sessionId) {
        if (this.processedAstItems.containsKey(node)) {
            return;
        }

        this.processedAstItems.put(node, node);

        if (node instanceof ImmutableList || node instanceof ConcatList) {
            Iterable list = (Iterable)node;

            // id -- [field] -> list
            this.storeReference(parent, predicate, list);
            this.storeType(list, "List");
            this.storeReference(parsedFilePath, "contains", list);
            this.handleIfInSession(sessionId, list);

            final Iterator iterator = list.iterator();
            int i = 0;

            Object prev = null;

            while (iterator.hasNext()) {
                Object el = iterator.next();
                this.processingQueue.add(new QueueItem(list, Integer.toString(i), el));

                if (prev != null) {
                    this.processingQueue.add(new QueueItem(prev, "_next", el));
                }
                prev = el;

                // connect the children directly
                this.processingQueue.add(new QueueItem(parent, predicate, el));

                i++;
            }

            this.storeReference(list, "last", prev);
//            createEndNode(list, sessionId);
        } else if (node instanceof Map) {
            Map map = (Map)node;

            if (!map.isEmpty()) {
                // id -- [field] -> table
                this.storeReference(parent, predicate, map);
//                storeType(map, node.getClass().getSimpleName());
                this.storeType(map, "Map");
                this.storeReference(parsedFilePath, "contains", map);
                handleIfInSession(sessionId, map);

                for (Object el : map.entrySet()) {
                    Map.Entry entry = (Map.Entry)el;
                    this.processingQueue.add(new QueueItem(map, entry.getKey().toString(), entry.getValue()));
                }
            }

        } else if (node instanceof HashTable) {

            HashTable table = (HashTable)node;

            if (table.length > 0) {
                // id -- [field] -> table
                this.storeReference(parent, predicate, table);
                this.storeType(table, "HashTable");
                this.storeReference(parsedFilePath, "contains", table);
                this.handleIfInSession(sessionId, table);

                for (Object el : table.entries()) {
                    Pair pair = (Pair)el;
                    this.processingQueue.add(new QueueItem(table, pair.left().toString(), pair.right()));
                }
            }

        }
    }

    private void handleFunctional(Object parent, String predicate, Object node, String sessionId) {
        if (this.processedAstItems.containsKey(node)) {
            return;
        }

        this.processedAstItems.put(node, node);

        if (node instanceof Maybe) {
            Maybe el = (Maybe)node;
            if (el.isJust()) {
                this.processingQueue.add(new QueueItem(parent, predicate, el.fromJust()));
//            } else {
//                processingQueue.add(new QueueItem(node, fieldName, "null"));
            }
        } else if (node instanceof Either) {
            Either el = (Either)node;
            if (el.isLeft()) {
                this.handleFunctional(parent, predicate, el.left(), sessionId);
            }
            if (el.isRight()) {
                this.handleFunctional(parent, predicate, el.right(), sessionId);
            }
        }
    }

    protected void handleAstNode(Object parent, String predicate, Object node, String sessionId) {
        if (parent != null) {
            this.storeReference(parent, predicate, node);
        }

        if (this.processedAstItems.containsKey(node)) {
            return;
        }

        this.processedAstItems.put(node, node);

//        createEndNode(node, sessionId);

        this.storeReference(parsedFilePath, "contains", node);
        this.handleIfInSession(sessionId, node);
        this.storeLocation(node);

        Class<?> nodeType = node.getClass();

        this.storeType(node, nodeType.getSimpleName());
        // list superclasses, interfaces
        List<Class<?>> interfaces = Arrays.asList(nodeType.getInterfaces());
        interfaces.forEach(elem -> {
            final String interfaceName = elem.getSimpleName();
            storeType(node, interfaceName);

            if (interfaceName.startsWith("Literal")) {
                storeType(node, "Literal");
            }
        });

        Class<?> superclass = nodeType.getSuperclass();
        while (superclass != Object.class) {
            this.storeType(node, superclass.getSimpleName());
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

                        // processingQueue.add(new QueueItem(node, fieldName, o.toString()));
                        this.storeProperty(node, fieldName, o.toString());

                    } else if (o instanceof Node) {

                        this.processingQueue.add(new QueueItem(node, fieldName, o));

                    } else if (fieldType.getName().startsWith("com.shapesecurity.functional")) {

                        // TODO
                        this.processingQueue.add(new QueueItem(node, fieldName, o));

                    } else {

                        // TODO
                        this.processingQueue.add(new QueueItem(node, fieldName, o));

                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    /**
     * If the sessionId is set, mark the node temporal and store the id.
     * Since we are only iterating one AST, there is no way a Node from another graph is mislabeled.
     *
     * @param sessionId
     * @param node
     */
    protected void handleIfInSession(String sessionId, Object node) {
        if (sessionId != null) {
            String nodeId = this.findOrCreate(node);
            this.queryBuilder.setLabel(nodeId, "Temp");
            this.queryBuilder.set(nodeId, "session", sessionId);
        }
    }

//    protected void createEndNode(Object node, String sessionId) {
//        Object end = new Object();
//        storeType(end, "End");
//        storeReference(node, "_end", end);
//        storeProperty(node, "session", sessionId);
//        storeReference(this.parsedFilePath, "contains", end);
//    }

    protected void storeLocation(Object node) {
        if (node instanceof com.shapesecurity.shift.ast.Node) {
            Maybe<SourceSpan> location = parserWithLocation.getLocation((com.shapesecurity.shift.ast.Node)node);
            if (location.isJust()) {
                this.processingQueue.add(new QueueItem(node, "location", location.fromJust()));
            }
        }
    }

    protected String findOrCreate(Object subject) {
        if (this.objectsWithAsgNodeIds.containsKey(subject)) {
            return this.objectsWithAsgNodeIds.get(subject);
        } else {
            String nodeId = this.queryBuilder.createUniqueIdentifierName();
            this.queryBuilder.merge(nodeId);
            this.objectsWithAsgNodeIds.put(subject, nodeId);
            return nodeId;
        }
    }

    protected String findOrCreate(Object subject, String[] labels, String[] attributes) {
        if (this.objectsWithAsgNodeIds.containsKey(subject)) {
            return this.objectsWithAsgNodeIds.get(subject);
        } else {
            String nodeId = this.queryBuilder.createUniqueIdentifierName();
            this.queryBuilder.merge(nodeId, labels, attributes);
            this.objectsWithAsgNodeIds.put(subject, nodeId);
            return nodeId;
        }
    }

    public void storeReference(Object subject, String predicate, Object object) {
        Preconditions.checkNotNull(subject);

        String nodeIdA = this.findOrCreate(subject);
        String nodeIdB = this.findOrCreate(object);

        this.queryBuilder.mergeConnection(nodeIdA, nodeIdB, predicate);
    }

    public void storeProperty(Object subject, String predicate, Object value) {
        Preconditions.checkNotNull(value);

        String nodeId = findOrCreate(subject);
        this.queryBuilder.set(nodeId, predicate, value.toString());
    }

    public void storeType(Object subject, String type) {
        Preconditions.checkNotNull(type);
        Preconditions.checkArgument(type.length() != 0);

        String nodeId = findOrCreate(subject);
        this.queryBuilder.setLabel(nodeId, type);
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
