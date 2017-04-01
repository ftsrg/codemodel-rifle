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
        "match",
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

    public String createUniqueIdentifierNameWithType(String type) {
        return this.createUniqueIdentifierName() + ":" + type;
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

    public QueryBuilder matches(String nodeName, String nodeType, Collection<String> wheres, Map<String, Object> parameters) {
        StringBuilder queryTemplate = new StringBuilder("MATCH");
        queryTemplate.append(" (");

        queryTemplate.append(nodeName);

        if (nodeType != null) {
            queryTemplate.append(":" + nodeType);
        }

        queryTemplate.append(") ");

        // Appending WHERE statements to the query.
        if (!wheres.isEmpty()) {
            queryTemplate.append("WHERE ");

            Iterator<String> i = wheres.iterator();
            while (i.hasNext()) {
                String whereStatement = i.next();
                queryTemplate.append(whereStatement);
                if (i.hasNext()) {
                    queryTemplate.append(" AND ");
                }
            }
        }

        // Parameters names' (stored as keys in the map) should be unique,
        // so we remove the parameter from the map by key,
        // create a new unique name to them, and put
        // them back to the parameters map.
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            String uniqueParameterKey = this.createUniqueIdentifierName();
            parameters.put(uniqueParameterKey, parameters.remove(parameter.getKey()));
        }

        Query q = new Query(queryTemplate.toString(), parameters);
        this.addQuery("match", q);

        return this;
    }
}
