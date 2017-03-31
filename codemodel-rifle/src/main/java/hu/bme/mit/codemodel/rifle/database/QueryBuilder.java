package hu.bme.mit.codemodel.rifle.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Optimizing queries' count by merging multiple queryies into one query.
 *
 * Planning to do merging by file-level granularity.
 */
public class QueryBuilder {

    /**
     * Queries are separated by parts, see:
     * https://neo4j.com/docs/cypher-refcard/current/
     *
     * Every query type has an individual list in which queries are stored as a string.
     */
    protected Map<String, List<String>> query;

    /**
     * The builder is configurable by specifying the query types and the ordering
     * of the individual types.
     *
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

    /**
     * At initializing the QueryBuilder, we initialize the individual query types' lists.
     */
    public QueryBuilder() {
        for (String queryTypeName : this.QUERY_TYPES_AND_ORDERING) {
            this.query.put(queryTypeName, new ArrayList<>());
        }
    }

    /**
     * Return the full query as a string.
     *
     * @return String
     */
    public String getQueryAsString() {
        List<String> finalQuery = new ArrayList<>();

        for (String queryTypeName : this.QUERY_TYPES_AND_ORDERING) {
            finalQuery.addAll(this.query.get(queryTypeName));
        }

        return String.join(" ", finalQuery);
    }

    /**
     * Add a new query to the builder.
     *
     * This method expects a query type name *match, merge, etc, see above)
     * and the query itself as a full query.
     *
     * @param queryTypeName
     * @param query
     *
     * @throws IllegalArgumentException
     */
    public void addQuery(String queryTypeName, String query) {
        try {
            this.query.get(queryTypeName).add(query);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The specified queryTypeName does not exist.");
        }
    }

    /**
     * Clear all queries from the builder.
     */
    public void clearBuilder() {
        for (Map.Entry<String, List<String>> queryType : this.query.entrySet()) {
            queryType.getValue().clear();
        }
    }
}
