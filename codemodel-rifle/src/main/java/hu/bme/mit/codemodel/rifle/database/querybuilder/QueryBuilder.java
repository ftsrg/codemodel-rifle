package hu.bme.mit.codemodel.rifle.database.querybuilder;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Optimizing queries' count by merging multiple queries into one fewer.
 * <p>
 * This custom QueryBuilder is built to support the ASTScopeProcessor making it more efficient.
 * It is not intended to be a general Cypher query builder engine.
 * <p>
 * We hand over the nodes we built in the ASTScopeProcessor to the QueryBuilder,
 * and the QueryBuilder generates efficient Cypher queries by merging and sorting,
 * e.g.: all node label and property can be set in one query, why should they be separated?
 * <p>
 * We try to execute as few queries as possible while having a balance between
 * query length and executed query number.
 */
public class QueryBuilder {
    /**
     * Settings to fine tune the builder.
     * <p>
     * Configures how many query body should be run in one query execution.
     */
    private static int createNodesQueriesIterationCount = 20;
    private static int setRelationsQueriesMaximumSetRelationshipCount = 1;

    /**
     * Creates a new query body for creating a node.
     *
     * @param node
     * @return
     */
    private static Query createCreateNodeQueryBody(AsgNode node) {
        StringBuilder stringBuilder = new StringBuilder();

        // (
        stringBuilder.append("(");

        // (`identifier`
        stringBuilder.append(String.format("`%s`", node.getId()));

        if (!node.getLabels().isEmpty()) {
            // (`identifier`:
            stringBuilder.append(":");

            String labelsAsString = String.join(":", node.getLabels().stream().map(label -> String.format("`%s`",
                label)).toArray(String[]::new));
            // (`identifier`:`label1`:`label2`...
            stringBuilder.append(labelsAsString);
        }

        Map<String, Object> statementParameters = new HashMap<>();
        List<String> propertiesWithParameterBindings = new ArrayList<>();
        if (!node.getProperties().isEmpty()) {
            // (`identifier`:`label1`:`label2`...{
            stringBuilder.append("{");

            for (Map.Entry<String, Object> property : node.getProperties().entrySet()) {
                String propertyName = property.getKey();
                String parameterBindingKey = Utils.createUniqueIdentifierName();
                String propertyString = String.format("`%s`:$`%s`", propertyName, parameterBindingKey);

                Object propertyValue = property.getValue();
                statementParameters.put(parameterBindingKey, propertyValue);

                propertiesWithParameterBindings.add(propertyString);
            }

            stringBuilder.append(String.join(",", propertiesWithParameterBindings));

            // (`identifier`:`label1`:`label2`...{`prop1`:$`prop1ParamBinding`,`prop2`:$`prop2ParamBinding`}
            stringBuilder.append("}");
        }

        // We will use the below seen body with a CREATE
        // (`identifier`:`label1`:`label2`...{`prop1`:$`prop1ParamBinding`',`prop2`:$`prop2ParamBinding`})
        stringBuilder.append(")");

        String statementTemplate = stringBuilder.toString();

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * We assemble the create queries for a list of nodes.
     * <p>
     * We create a query which creates the given nodes in the following form:
     * CREATE (`identifier`:`label1`:`label2`...{`prop1`:'{`prop1ParamBinding`}',`prop2`:'{`prop2ParamBinding`}'}), etc.
     *
     * @return Query
     */
    private static Query getCreateNodesQuery(Collection<AsgNode> nodes) {
        List<Query> queryBodies = new ArrayList<>();
        nodes.forEach(node -> queryBodies.add(createCreateNodeQueryBody(node)));

        String statementTemplate = "CREATE" + String.join(",", queryBodies.stream().map(Query::getStatementTemplate).toArray(String[]::new));
        Map<String, Object> statementParameters = new HashMap<>();
        queryBodies.forEach(queryBody -> statementParameters.putAll(queryBody.getStatementParameters()));

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * Creates a relationship query.
     *
     * MATCH (`from` {`id`: $`fromId`}), (`to`: {`id`: $`toId`})
     * CREATE (`from`)-[:`relationshipLabel`]->(`to`)
     *
     * @param relation
     * @return
     */
    private static Query createRelationSingleQuery(AsgRelation relation) {
        String statementTemplate = String.format("MATCH(`from`{`id`:$`fromId`}),(`to`{`id`:$`toId`})CREATE(`from`)" +
                "-[:`%s`]->(`to`)", relation.getRelationshipLabel());

        Map<String, Object> statementParameters = new HashMap<>();
        statementParameters.put("fromId", relation.getFromNode().getId());
        statementParameters.put("toId", relation.getToNode().getId());

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * The query builder has two basic jobs: the first is to create queries
     * to create the nodes with the provided labels and properties.
     *
     * This returns a list of queries: each one can be executed without further modifications.
     * Multiple node creating queries merged into fewer queries configured above.
     *
     * @param nodes
     * @return List
     */
    public static List<Query> getCreateNodeQueries(List<AsgNode> nodes) {
        List<Query> ret = new ArrayList<>();

        // We slice up the query bodies' list by the configured number,
        // so this many queries will be executed in one turn.
        Lists.partition(nodes, createNodesQueriesIterationCount).forEach(fewerNodes -> ret.add(getCreateNodesQuery(fewerNodes)));

        return ret;
    }

    /**
     * The query builder has two basic jobs: the second if to create queries
     * to set the relationships between nodes.
     *
     * This returns a list of queries: each one can be executed without further modifications.
     * Multiple relationship setting queries are merged into fewer queries configured above.
     *
     * @param nodes
     * @return
     */
    public static List<Query> getSetRelationshipQueries(List<AsgNode> nodes) {
        List<Query> ret = new ArrayList<>();

        // Collecting all node-node relations into a list.
        List<AsgRelation> relations = new ArrayList<>();
        nodes.forEach(node -> relations.addAll(node.getRelations()));

        // Executing one relationship setting query for each node.
        relations.forEach(relation -> ret.add(createRelationSingleQuery(relation)));

        return ret;
    }
}
