package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.*;

/**
 * Optimizing queries' count by merging multiple queries into one queriesMappedByType.
 * <p>
 * Planning to do merging by file-level granularity.
 */
public class QueryBuilder {

    /**
     * Queries are separated by parts, see:
     * https://neo4j.com/docs/cypher-refcard/current/
     * <p>
     * Every query type has an individual list in which queries are stored as a Query.
     */
    protected Map<String, List<Query>> queriesMappedByType = new HashMap<>();

    /**
     * The builder is configurable by specifying the queriesMappedByType types and the ordering
     * of the individual types.
     * <p>
     * See: https://s3.amazonaws.com/artifacts.opencypher.org/railroad/Cypher.html
     */
    protected final String[] QUERY_TYPES_AND_ORDERING = {
        "matchWhere",
        "unwind",
        "merge",
        "create",
        "set",
        "delete",
        "remove",
        "with",
        "return"
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

    public String createUniqueIdentifierName() {
        return "_" + UUID.randomUUID().toString().replace("-", "");
    }

    public Query getQuery() {
        Query finalQuery = new Query();

        for (String queryTypeName : this.QUERY_TYPES_AND_ORDERING) {
            for (Query q : this.queriesMappedByType.get(queryTypeName)) {
                finalQuery.append(q.getStatementTemplate(), q.getStatementParameters());
            }
        }

        return finalQuery;
    }

    /**
     * Creates and appends a basic match.
     * <p>
     * Returns self making the builder chainable.
     *
     * @param nodeName
     * @return
     */
    public QueryBuilder match(String nodeName) {
        String queryTemplate = String.format("MATCH (%s)", nodeName);

        Query q = new Query(queryTemplate, new HashMap<>());
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Creates a basic match query with a node type specificaiton.
     *
     * @param nodeName
     * @param nodeType
     * @return
     */
    public QueryBuilder match(String nodeName, String nodeType) {
        String queryTemplate = String.format("MATCH (%s:%s)", nodeName, nodeType);

        Query q = new Query(queryTemplate, new HashMap<>());
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds a where statement to the query.
     *
     * @param nodeName
     * @param nodeProperty
     * @param value
     * @return
     */
    public QueryBuilder where(String nodeName, String nodeProperty, String value) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("WHERE %s.%s = {%s}", nodeName, nodeProperty, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBinding, value);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds a where statement to the query with a custom operator.
     *
     * @param nodeName
     * @param nodeProperty
     * @param operator
     * @param value
     * @return
     */
    public QueryBuilder where(String nodeName, String nodeProperty, String operator, String value) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("WHERE %s.%s %s {%s}", nodeName, nodeProperty, operator, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBinding, value);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds an additional where statement to the query with the AND logical operator.
     *
     * @param nodeName
     * @param nodeProperty
     * @param value
     * @return
     */
    public QueryBuilder andWhere(String nodeName, String nodeProperty, String value) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("AND %s.%s = {%s}", nodeName, nodeProperty, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBinding, value);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds an additional where statement to the query with the AND logical operator with a custom operator.
     *
     * @param nodeName
     * @param nodeProperty
     * @param operator
     * @param value
     * @return
     */
    public QueryBuilder andWhere(String nodeName, String nodeProperty, String operator, String value) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("AND %s.%s %s {%s}", nodeName, nodeProperty, operator, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBinding, value);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds an additional where statement to the query with the OR logical operator.
     *
     * @param nodeName
     * @param nodeProperty
     * @param value
     * @return
     */
    public QueryBuilder orWhere(String nodeName, String nodeProperty, String value) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("OR %s.%s = {%s}", nodeName, nodeProperty, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBinding, value);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds an additional where statement to the query with the OR logical operator with a custom operator.
     *
     * @param nodeName
     * @param nodeProperty
     * @param operator
     * @param value
     * @return
     */
    public QueryBuilder orWhere(String nodeName, String nodeProperty, String operator, String value) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("OR %s.%s %s {%s}", nodeName, nodeProperty, operator, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameterBinding, value);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Adds a where statement for finding a node by id.
     *
     * @param nodeName
     * @param id
     * @return
     */
    public QueryBuilder whereId(String nodeName, long id) {
        String queryTemplate = String.format("WHERE id(%s) = %d", nodeName, id);
        Map<String, Object> parameters = new HashMap<>();

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("matchWhere", q);

        return this;
    }

    /**
     * Sets the label of the specified node.
     * <p>
     * Returns self making the builder chainable.
     *
     * @param nodeName
     * @param newType
     * @return
     */
    public QueryBuilder setLabel(String nodeName, String newType) {
        String queryTemplate = String.format("SET %s:%s", nodeName, newType);
        Query q = new Query(queryTemplate, new HashMap<>());
        this.addQuery("set", q);
        return this;
    }

    /**
     * Sets a property value of a node.
     * <p>
     * Returns self, making the builder chainable.
     *
     * @param nodeName
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public QueryBuilder set(String nodeName, String propertyName, String propertyValue) {
        String parameterBinding = this.createUniqueIdentifierName();

        String queryTemplate = String.format("SET %s.%s = {%s}", nodeName, propertyName, parameterBinding);
        Map<String, Object> parameters = new HashMap<>();
        // We create a unique parameter binding name for each appended parameters.
        parameters.put(parameterBinding, propertyValue);

        Query q = new Query(queryTemplate, parameters);
        this.addQuery("set", q);
        return this;
    }
}
