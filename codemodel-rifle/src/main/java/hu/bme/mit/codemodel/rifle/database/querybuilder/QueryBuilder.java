package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.*;

/**
 * Optimizing queries' count by merging multiple queries into one queriesMappedByType.
 * <p>
 * Planning to do merging by file-level granularity.
 */
public class QueryBuilder {

    protected Map<String, List<String>> nodesWithTypeSettings = new HashMap<>();
    protected Map<String, List<String>> nodesWithPropertySettings = new HashMap<>();

    /**
     * Queries are separated by parts, see:
     * https://neo4j.com/docs/cypher-refcard/current/
     * <p>
     * Every query type has an individual list in which queries are stored as a Query.
     */
    public Map<String, List<Query>> queriesMappedByType = new HashMap<>();

    /**
     * The builder is configurable by specifying the queriesMappedByType types and the ordering
     * of the individual types.
     * <p>
     * See: https://s3.amazonaws.com/artifacts.opencypher.org/railroad/Cypher.html
     */
    public final String[] QUERY_TYPES_AND_ORDERING = {
        "merge",
        "set",
    };

    protected void initialize() {
        for (String queryTypeName : this.QUERY_TYPES_AND_ORDERING) {
            this.queriesMappedByType.put(queryTypeName, new ArrayList<>());
        }
    }

    /**
     * At initializing the QueryBuilder, we initialize the individual query types' lists.
     */
    public QueryBuilder() {
        this.initialize();
    }

    /**
     * Add a new queriesMappedByType to the builder.
     * <p>
     * This method expects a queriesMappedByType type name *match, merge, etc, see above)
     * and the queriesMappedByType itself as a full queriesMappedByType.
     *
     * @param queryTypeName
     * @param query
     * @throws IllegalArgumentException
     */
    protected void addQuery(String queryTypeName, Query query) {
        try {
            this.queriesMappedByType.get(queryTypeName).add(query);
            System.out.println(query.toString());
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The specified queryTypeName does not exist.");
        }
    }

    /**
     * Clear all queries from the builder.
     */
    public void clearBuilder() {
        for (Map.Entry<String, List<Query>> queryType : this.queriesMappedByType.entrySet()) {
            queryType.getValue().clear();
        }
    }

    /**
     * Creates a unique node identifier.
     *
     * @return String
     */
    public String createUniqueIdentifierName() {
        return String.format("_%s", UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * Gets the query built in the query builder.
     *
     * @return Query
     */
    public Query getQuery() {
        Query finalQuery = new Query();

        for (Query q : this.queriesMappedByType.get("merge")) {
            finalQuery.append(q.getStatementTemplate(), q.getStatementParameters());
        }

        for (Map.Entry<String, List<String>> nodeWithTypes : this.nodesWithTypeSettings.entrySet()) {
            String[] types = Arrays.copyOf(nodeWithTypes.getValue().toArray(), nodeWithTypes.getValue().toArray()
                .length, String[].class);
            Query q = this.createSetLabelsQuery(nodeWithTypes.getKey(), types);
            System.out.println(q.toString());
            finalQuery.append(q.getStatementTemplate(), q.getStatementParameters());
        }

        for (Map.Entry<String, List<String>> nodeWithProperties : this.nodesWithPropertySettings.entrySet()) {
            String[] properties = Arrays.copyOf(nodeWithProperties.getValue().toArray(), nodeWithProperties.getValue
                ().toArray().length, String[].class);
            Query q = this.createSetPropertiesQuery(nodeWithProperties.getKey(), properties);
            System.out.println(q.toString());
            finalQuery.append(q.getStatementTemplate(), q.getStatementParameters());
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(finalQuery.toString());

        return finalQuery;
    }

    protected Query createSetLabelsQuery(String nodeId, String[] labels) {
        String labelsAsString = String.join(":", Arrays.stream(labels).map(label -> String.format("`%s`", label))
            .toArray(String[]::new));

        String queryTemplate = String.format("SET `%s`:%s", nodeId, labelsAsString);
        Query q = new Query(queryTemplate, new HashMap<>());
        return q;
    }

    protected Query createSetPropertiesQuery(String nodeId, String[] properties) {
        Map<String, Object> parameters = new HashMap<>();
        List<String> propertiesWithParameterBindingKeys = new ArrayList<>();
        for (String property : properties) {
            String[] propertySplit = property.split(":");
            String propertyName = propertySplit[0];

            String parameterBindingKey = this.createUniqueIdentifierName();
            String propertyValue = propertySplit[1];
            parameters.put(parameterBindingKey, propertyValue);

            String propertyString = String.format("`%s`.`%s` = {`%s`}", nodeId, propertyName, parameterBindingKey);
            propertiesWithParameterBindingKeys.add(propertyString);
        }

        String queryTemplate = String.format("SET %s", String.join(", ",
            propertiesWithParameterBindingKeys));
        Query q = new Query(queryTemplate, parameters);
        return q;
    }

    /**
     * Sets a property value of a node.
     * <p>
     * Returns self, making the builder chainable.
     *
     * @param nodeId
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public QueryBuilder set(String nodeId, String propertyName, String propertyValue) {
//        String parameterBinding = this.createUniqueIdentifierName();
//
//        String queryTemplate = String.format("SET `%s`.`%s` = {`%s`}", nodeId, propertyName, parameterBinding);
//        Map<String, Object> parameters = new HashMap<>();
//        // We create a unique parameter binding name for each appended parameters.
//        parameters.put(parameterBinding, propertyValue);
//
//        Query q = new Query(queryTemplate, parameters);
//        this.addQuery("set", q);
//        return this;

        if (!this.nodesWithPropertySettings.containsKey(nodeId)) {
            List<String> properties = new ArrayList<>();
            properties.add(String.format("%s:%s", propertyName, propertyValue));
            this.nodesWithPropertySettings.put(nodeId, properties);
        } else {
            this.nodesWithPropertySettings.get(nodeId).add(String.format("%s:%s", propertyName, propertyValue));
        }

        return this;
    }

    /**
     * Sets the label of the specified node.
     * <p>
     * Returns self making the builder chainable.
     *
     * @param nodeId
     * @param newType
     * @return
     */
    public QueryBuilder setLabel(String nodeId, String newType) {
        if (!this.nodesWithTypeSettings.containsKey(nodeId)) {
            List<String> list = new ArrayList<>();
            list.add(newType);
            this.nodesWithTypeSettings.put(nodeId, list);
        } else {
            this.nodesWithTypeSettings.get(nodeId).add(newType);
        }

        return this;
    }

    /**
     * Creates a node with the given ID.
     *
     * @param nodeId
     * @return
     */
    public QueryBuilder merge(String nodeId) {
        String parameterBindingKey = this.createUniqueIdentifierName();
        String queryTemplate = String.format("MERGE (`%s` {`id`: {`%s`}})", nodeId, parameterBindingKey);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBindingKey, nodeId);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("merge", q);

        return this;
    }

    /**
     * Merges a node with the given labels and properties.
     * <p>
     * Properties should be in the following form:
     * { "propertyName1:propertyValue1", "propertyName2:propertyValue2", "propertyName3:propertyValue3" }
     *
     * @param nodeId
     * @return
     */
    public QueryBuilder merge(String nodeId, String[] labels, String[] properties) {
        String labelsAsString = String.join(":", Arrays.stream(labels).map(label -> String.format("`%s`", label))
            .toArray(String[]::new));

        Map<String, Object> parameters = new HashMap<>();
        List<String> propertiesWithParameterBindingKeys = new ArrayList<>();
        for (String property : properties) {
            String[] propertySplit = property.split(":");
            String propertyName = propertySplit[0];

            String parameterBindingKey = this.createUniqueIdentifierName();
            String propertyValue = propertySplit[1];
            parameters.put(parameterBindingKey, propertyValue);

            String propertyString = String.format("`%s`: {`%s`}", propertyName, parameterBindingKey);
            propertiesWithParameterBindingKeys.add(propertyString);
        }

        String queryTemplate = String.format("MERGE (`%s`:%s {%s})", nodeId, labelsAsString, String.join(", ",
            propertiesWithParameterBindingKeys));

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("merge", q);

        return this;
    }

    /**
     * Creates a connection between two nodes.
     * <p>
     * Does not explicitly check node ID, can be used only chained after a merge or a match.
     *
     * @param nodeIdFrom
     * @param nodeIdTo
     * @param connectionLabel
     * @return
     */
    public QueryBuilder mergeConnection(String nodeIdFrom, String nodeIdTo, String connectionLabel) {
        String queryTemplate = String.format("MERGE (`%s`)-[:`%s`]->(`%s`)", nodeIdFrom, connectionLabel, nodeIdTo);

        Query q = new Query(queryTemplate, new HashMap<>());
        this.addQuery("merge", q);

        return this;
    }
}
