package hu.bme.mit.codemodel.rifle.database;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.base.Stopwatch;
import hu.bme.mit.codemodel.rifle.database.querybuilder.AsgNode;
import hu.bme.mit.codemodel.rifle.database.querybuilder.Query;
import hu.bme.mit.codemodel.rifle.database.querybuilder.QueryBuilder;
import org.neo4j.driver.v1.Transaction;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.parser.SourceSpan;
import com.shapesecurity.shift.parser.ParserWithLocation;
import com.shapesecurity.shift.scope.Scope;

/**
 * After processing, the parsed AST becomes an ASG.
 */
public class ASTScopeProcessor {
    /**
     * Path of the parsed file.
     */
    private final String parsedFilePath;

    /**
     * A Shape's parser with location.
     */
    private final ParserWithLocation parserWithLocation;

    /**
     * AST items that are already traversed and processed.
     */
    private final Map<Object, Object> processedAstItems = new IdentityHashMap<>();

    /**
     * AST objects with ASG nodes.
     */
    private final Map<Object, AsgNode> objectsWithAsgNodes = new HashMap<>();

    /**
     * The processing queue.
     */
    private final BlockingQueue<QueueItem> processingQueue = new LinkedBlockingQueue<>();

    /**
     * Internal class for processing.
     */
    protected class QueueItem {
        Object subject;
        Object parent;
        String label;

        QueueItem(Object subject, Object parent, String label) {
            this.subject = subject;
            this.parent = parent;
            this.label = label;
        }
    }

    /**
     * Default constructor.
     *
     * @param parsedFilePath
     * @param parserWithLocation
     */
    public ASTScopeProcessor(String parsedFilePath, ParserWithLocation parserWithLocation) {
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
    public void processScope(Scope scope, String sessionId, Transaction tx) {
        this.createFilePathNode(sessionId);
        processingQueue.add(new QueueItem(scope, null, null));

        final Logger logger = Logger.getLogger("codemodel");

        Stopwatch stopwatch = Stopwatch.createUnstarted();

        stopwatch.start();
        try {
            while (!processingQueue.isEmpty()) {
                QueueItem queueItem = processingQueue.take();
                process(queueItem, sessionId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long mappingDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info(String.format("%s %s %dms", parsedFilePath, "MAPPING", mappingDone));
        stopwatch.reset();

        final List<AsgNode> asgNodes = new ArrayList<>(this.objectsWithAsgNodes.values());

        try {
            List<Query> queriesToRun = new ArrayList<>();

            queriesToRun.addAll(QueryBuilder.getCreateNodeQueries(asgNodes));

            stopwatch.start();
            for (Query q : queriesToRun) {
                tx.run(q.getStatementTemplate(), q.getStatementParameters());
            }
            long createDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info(
                String.format("%s %s (%s query executed) %dms", parsedFilePath, "CREATE", queriesToRun.size(),
                    createDone)
            );
            stopwatch.reset();

            queriesToRun.clear();
            queriesToRun.addAll(QueryBuilder.getSetRelationshipQueries(asgNodes));

            stopwatch.start();
            for (Query q : queriesToRun) {
                tx.run(q.getStatementTemplate(), q.getStatementParameters());
            }
            long relationshipDone = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info(
                String.format("%s %s (%s query executed) %dms", parsedFilePath, "RELATIONSHIPS", queriesToRun.size(),
                    relationshipDone)
            );
            stopwatch.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the file path node with the label CompilationUnit.
     *
     * @param sessionId
     */
    private void createFilePathNode(String sessionId) {
        this.findOrCreate(parsedFilePath, new String[]{ "CompilationUnit" }, new String[]{ "parsedFilePath:" +
            parsedFilePath, "session:" + sessionId });
    }

    /**
     * Processes an item from the queue, creates the ASG nodes, sets labels, properties and connections.
     *
     * @param queueItem
     * @param sessionId
     */
    private void process(QueueItem queueItem, String sessionId) {
        Object subject = queueItem.subject;
        Object parent = queueItem.parent;
        String label = queueItem.label;

        // TODO for presentation reasons the Nil node is hidden
        if (subject == null) {
            return;
        }

        if (isPrimitive(subject.getClass())) {
            // If it is a primitive type, we have to simply store it with its parent.
            this.storeProperty(parent, label, subject);
        } else if (isCollection(subject)) {
            // If it is a collection, we have to open it up and store all members.
            this.processCollection(subject, parent, label, sessionId);
        } else if (subject instanceof Maybe || subject instanceof Either) {
            // If it is a functional (if, else, etc.), we have to look at each branches.
            this.processFunctional(subject, parent, label, sessionId);
        } else if (subject.getClass().getName().startsWith("com.shapesecurity")) {
            // In every other general cases, we have to handle it as an AST node.
            this.processAstNode(subject, parent, label, sessionId);
        } else {
            System.err.println("Unexpected object type.");
        }
    }

    /**
     * Tells if a Class is primitive. We consider String as primitive as well.
     * <p>
     * See: http://stackoverflow.com/questions/209366/how-can-i-generically-tell-if-a-java-class-is-a-primitive-type
     *
     * @param c
     * @return
     */
    private static boolean isPrimitive(Class c) {
        if (c.isPrimitive()) {
            return true;
        } else if (c == Byte.class
            || c == Short.class
            || c == Integer.class
            || c == Long.class
            || c == Float.class
            || c == Double.class
            || c == Boolean.class
            || c == Character.class
            || c == String.class
            ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tells if an Object is a Collection or not.
     *
     * @param subject
     * @return
     */
    private static boolean isCollection(Object subject) {
        return subject instanceof ImmutableList
            || subject instanceof Map
            || subject instanceof HashTable
            || subject instanceof ConcatList;
    }

    /**
     * Processes a collection.
     *
     * @param subject
     * @param parent
     * @param label
     * @param sessionId
     */
    private void processCollection(Object subject, Object parent, String label, String sessionId) {
        if (this.processedAstItems.containsKey(subject)) {
            return;
        }

        this.processedAstItems.put(subject, subject);

        if (subject instanceof ImmutableList || subject instanceof ConcatList) {
            Iterable list = (Iterable)subject;

            // id -- [field] -> list
            final Iterator iterator = list.iterator();
            Object prev = null;
            while (iterator.hasNext()) {
                Object el = iterator.next();

                if (prev != null) {
                    this.processingQueue.add(new QueueItem(el, prev, "_next"));
                }
                prev = el;

                // connect the children directly
                this.processingQueue.add(new QueueItem(el, parent, label));
            }
        } else if (subject instanceof Map) {
            Map map = (Map)subject;

            if (!map.isEmpty()) {
                // id -- [field] -> table
                this.storeReference(parent, map, label);
//                this.storeType(map, parent.getClass().getSimpleName());
                this.storeType(map, "Map");
                this.storeReference(parsedFilePath, map, "contains");
                this.handleIfInSession(sessionId, map);

                for (Object el : map.entrySet()) {
                    Map.Entry entry = (Map.Entry)el;
                    this.processingQueue.add(new QueueItem(entry.getValue(), map, entry.getKey().toString()));
                }
            }
        } else if (subject instanceof HashTable) {
            HashTable table = (HashTable)subject;

            if (table.length > 0) {
                // id -- [field] -> table
                this.storeReference(parent, table, label);
                this.storeType(table, "HashTable");
                this.storeReference(parsedFilePath, table, "contains");
                this.handleIfInSession(sessionId, table);

                for (Object el : table.entries()) {
                    Pair pair = (Pair)el;
                    this.processingQueue.add(new QueueItem(pair.right(), table, pair.left().toString()));
                }
            }
        }
    }

    /**
     * Processes a functional item like an if.
     *
     * @param subject
     * @param parent
     * @param label
     * @param sessionId
     */
    private void processFunctional(Object subject, Object parent, String label, String sessionId) {
        if (this.processedAstItems.containsKey(subject)) {
            return;
        }

        this.processedAstItems.put(subject, subject);

        if (subject instanceof Maybe) {
            Maybe el = (Maybe)subject;
            if (el.isJust()) {
                this.processingQueue.add(new QueueItem(el.fromJust(), parent, label));
//            } else {
//                processingQueue.add(new QueueItem(node, fieldName, "null"));
            }
        } else if (subject instanceof Either) {
            Either el = (Either)subject;
            if (el.isLeft()) {
                this.processFunctional(el.left(), parent, label, sessionId);
            }
            if (el.isRight()) {
                this.processFunctional(el.right(), parent, label, sessionId);
            }
        }
    }

    /**
     * Processes an AST node.
     *
     * @param subject
     * @param parent
     * @param sessionId
     */
    protected void processAstNode(Object subject, Object parent, String label, String sessionId) {
        if (parent != null) {
            this.storeReference(parent, subject, label);
        }

        if (this.processedAstItems.containsKey(subject)) {
            return;
        }

        this.processedAstItems.put(subject, subject);

        createEndNode(subject, sessionId);

        this.storeReference(parsedFilePath, subject, "contains");
        this.handleIfInSession(sessionId, subject);
        this.storeLocation(subject);

        Class<?> nodeType = subject.getClass();

        this.storeType(subject, nodeType.getSimpleName());
        // list superclasses, interfaces
        List<Class<?>> interfaces = Arrays.asList(nodeType.getInterfaces());
        interfaces.forEach(elem -> {
            final String interfaceName = elem.getSimpleName();
            storeType(subject, interfaceName);

            if (interfaceName.startsWith("Literal")) {
                storeType(subject, "Literal");
            }
        });

        Class<?> superclass = nodeType.getSuperclass();
        while (superclass != Object.class) {
            this.storeType(subject, superclass.getSimpleName());
            superclass = superclass.getSuperclass();
        }


        getAllFields(nodeType).forEach(
            field -> {
                field.setAccessible(true);

                Class<?> fieldType = field.getType();
                String fieldName = field.getName();


                try {
                    Object o = field.get(subject);
                    if (fieldType.isEnum()) {

                        // processingQueue.add(new QueueItem(node, fieldName, o.toString()));
                        this.storeProperty(subject, fieldName, o.toString());

                    } else if (o instanceof AsgNode) {

                        this.processingQueue.add(new QueueItem(o, subject, fieldName));

                    } else if (fieldType.getName().startsWith("com.shapesecurity.functional")) {

                        // TODO
                        this.processingQueue.add(new QueueItem(o, subject, fieldName));

                    } else {

                        // TODO
                        this.processingQueue.add(new QueueItem(o, subject, fieldName));

                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    /**
     * If the sessionId is set, mark the node temporal and store the id.
     * Since we are only iterating one AST, there is no way a AsgNode from another graph is mislabeled.
     *
     * @param sessionId
     * @param subject
     */
    private void handleIfInSession(String sessionId, Object subject) {
        if (sessionId != null) {
            AsgNode node = this.findOrCreate(subject);
            node.addLabel("Temp");
            node.addProperty("session", sessionId);
        }
    }

    /**
     * Stores the location property of an AST object.
     *
     * @param subject
     */
    private void storeLocation(Object subject) {
        if (subject instanceof com.shapesecurity.shift.ast.Node) {
            Maybe<SourceSpan> location = parserWithLocation.getLocation((com.shapesecurity.shift.ast.Node)subject);
            if (location.isJust()) {
                this.processingQueue.add(new QueueItem(location.fromJust(), subject, "location"));
            }
        }
    }

    /**
     * Finds or creates a AsgNode object for the AST object.
     *
     * @param subject
     * @return AsgNode
     */
    private AsgNode findOrCreate(Object subject) {
        if (this.objectsWithAsgNodes.containsKey(subject)) {
            return this.objectsWithAsgNodes.get(subject);
        } else {
            AsgNode node = new AsgNode();
            this.objectsWithAsgNodes.put(subject, node);
            return node;
        }
    }

    /**
     * Finds or creates a AsgNode object for the AST object with attributes.
     *
     * @param subject
     * @param labels
     * @param properties
     * @return AsgNode
     */
    private AsgNode findOrCreate(Object subject, String[] labels, String[] properties) {
        if (this.objectsWithAsgNodes.containsKey(subject)) {
            return this.objectsWithAsgNodes.get(subject);
        } else {
            AsgNode node = new AsgNode();
            Arrays.asList(labels).forEach(node::addLabel);
            Arrays.asList(properties).forEach(property -> {
                String[] propertySplit = property.split(":");
                String propertyName = propertySplit[0];
                String propertyValue = propertySplit[1];
                node.addProperty(propertyName, propertyValue);
            });
            this.objectsWithAsgNodes.put(subject, node);
            return node;
        }
    }

    /**
     * Stores a reference to another node.
     *
     * @param from
     * @param to
     * @param referenceLabel
     */
    private void storeReference(Object from, Object to, String referenceLabel) {
        AsgNode nodeFrom = this.findOrCreate(from);
        AsgNode nodeTo = this.findOrCreate(to);

        if (!nodeFrom.hasRelation(nodeTo, referenceLabel)) {
            nodeFrom.addRelation(nodeTo, referenceLabel);
        }
    }

    /**
     * Stores a property of an AST object.
     *
     * @param subject
     * @param propertyName
     * @param propertyValue
     */
    private void storeProperty(Object subject, String propertyName, Object propertyValue) {
        if (subject == null) {
            return;
        }

        AsgNode node = this.findOrCreate(subject);

        Class propertyValueClass = propertyValue.getClass();

        if (propertyValueClass == Boolean.class ||
            propertyValueClass == Long.class ||
            propertyValueClass == Double.class) {
            node.addProperty(propertyName, propertyValue);
        } else {
            node.addProperty(propertyName, propertyValue.toString());
        }
    }

    /**
     * Stores the type of an AST object.
     *
     * @param subject
     * @param type
     */
    private void storeType(Object subject, String type) {
        AsgNode node = this.findOrCreate(subject);
        node.addLabel(type);
    }

    /**
     * Creates and end node for the CFG.
     *
     * @param subject
     * @param sessionId
     */
    private void createEndNode(Object subject, String sessionId) {
        Object end = new Object();
        this.storeType(end, "End");
        this.storeReference(subject, end, "_end");
        this.storeProperty(subject, "session", sessionId);
        this.storeProperty(this.parsedFilePath, "contains", end);
    }

    /**
     * Gets all (incl. private) fields of a class, and returns it in a list.
     * <p>
     * See: http://stackoverflow.com/questions/3567372/access-to-private-inherited-fields-via-reflection-in-java
     *
     * @param subjectClass
     * @return List
     */
    private List<Field> getAllFields(Class subjectClass) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(subjectClass.getDeclaredFields()));

        Class superClass = subjectClass.getSuperclass();
        if (superClass != null) {
            fields.addAll(getAllFields(superClass));
        }

        return fields;
    }
}
