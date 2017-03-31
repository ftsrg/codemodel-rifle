package hu.bme.mit.codemodel.rifle.database;

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
     *
     * @throws IllegalArgumentException
     */
    public void addQuery(String queryTypeName, Query query) {
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

    public Query getQuery() {
        Query finalQuery = new Query();

        for (String queryTypeName : this.QUERY_TYPES_AND_ORDERING) {
            for (Query q : this.queriesMappedByType.get(queryTypeName)) {
                finalQuery.append(q.getStatementTemplate(), q.getStatementParameters());
            }
        }

        return finalQuery;
    }
}
