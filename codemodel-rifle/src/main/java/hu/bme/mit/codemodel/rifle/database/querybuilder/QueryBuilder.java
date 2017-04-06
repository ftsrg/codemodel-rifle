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
//    private static int setRelationsQueriesIterationCount = 1;

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
     * We assemble the create queries for every node.
     * <p>
     * We create query bodies with query parameters in the following form:
     * (`identifier`:`label1`:`label2`...{`prop1`:'{`prop1ParamBinding`}',`prop2`:'{`prop2ParamBinding`}'})
     * so we get a list of query bodies with the bound parameters.
     * <p>
     * This way we can configure later how many queries to run at once.
     *
     * @return List
     */
    private static List<Query> getCreateNodesQueryBodies(Collection<AsgNode> nodes) {
        List<Query> ret = new ArrayList<>();

        nodes.forEach(node -> ret.add(createCreateNodeQueryBody(node)));

        return ret;
    }

    /**
     * Creates a relationship query body between two nodes.
     *
     * MATCH (`from` {`id`: $`fromId`}), (`to`: {`id`: $`toId`})
     * CREATE (`from`)-[:`relationshipLabel`]->(`to`)
     *
     * @param from
     * @param to
     * @param relationshipLabel
     * @return
     */
    private static Query createRelationQueryBetweenNodes(AsgNode from, AsgNode to, String relationshipLabel) {
        String statementTemplate = String.format("MATCH(`from`{`id`:$`fromId`}),(`to`{`id`:$`toId`})CREATE(`from`)" +
                "-[:`%s`]->(`to`)", relationshipLabel);

        Map<String, Object> statementParameters = new HashMap<>();
        statementParameters.put("fromId", from.getId());
        statementParameters.put("toId", to.getId());

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * Creates a relationship query matching the node and all related nodes,
     * and then creating the relationships.
     *
     * MATCH (`node` {`id`: $`nodeId`}), (`relatedNode1`: {`id`: $`relatedNode1Id`}), (`relatedNode2`: {`id`: $`relatedNode2Id`}), ...
     * CREATE (`node`)-[:`relationshipLabel`]->(`relatedNode1`), (`node`)-[:`relationshipLabel`]->(`relatedNode2`), ...
     *
     * @param node
     * @return
     */
    private static Query createRelationQueryBetweenNodeAndAllRelatedNode(AsgNode node) {
        StringBuilder statementTemplate = new StringBuilder();
        Map<String, Object> statementParameters = new HashMap<>();

        // We have to match all nodes before we can create the relationships.
        // At first we the node itself.
        String nodeId = node.getId();
        String nodeIdBinding = Utils.createUniqueIdentifierName();
        statementTemplate.append(String.format("MATCH(`%s`{`id`:$`%s`}),", nodeId, nodeIdBinding));
        statementParameters.put(nodeIdBinding, nodeId);

        // After that, we match the related nodes.
        statementTemplate.append(node.getReferences().entrySet().stream().map(relation -> {
            String relatedNodeId = relation.getKey().getId();
            String relatedNodeIdBinding = Utils.createUniqueIdentifierName();
            statementParameters.put(relatedNodeIdBinding, relatedNodeId);
            return String.format("(`%s`{`id`:$`%s`})", relatedNodeId, relatedNodeIdBinding);
        }).collect(Collectors.joining(",")));

        // Then, we can start creating the relationships.
        statementTemplate.append("CREATE");
        statementTemplate.append(node.getReferences().entrySet().stream().map(relation -> {
            String relatedNodeId = relation.getKey().getId();
            String relationshipLabel = relation.getValue();
            return String.format("(`%s`)-[:`%s`]->(`%s`)", nodeId, relationshipLabel, relatedNodeId);
        }).collect(Collectors.joining(",")));

        return new Query(statementTemplate.toString(), statementParameters);
    }

    /**
     * The query builder has two basic jobs: the first is to create queries
     * to create the nodes with the provided labels and properties.
     *
     * This returns a list of queries: each one can be executed without further modifications.
     * Multiple node creating queries merged into fewer queries configured above.
     *
     * @param nodes
     * @return
     */
    public static List<Query> getCreateNodeQueries(Collection<AsgNode> nodes) {
        List<Query> ret = new ArrayList<>();

        List<Query> createNodesQueryBodies = getCreateNodesQueryBodies(nodes);
        // We slice up the query bodies' list by the configured number,
        // so this many queries will be executed in one turn.
        Lists.partition(createNodesQueryBodies, createNodesQueriesIterationCount).forEach(queryBodiesPartitionedList
            -> {
            String statementTemplate = "CREATE" + String.join(",", queryBodiesPartitionedList.stream().map
                (Query::getStatementTemplate).toArray(String[]::new));
            Map<String, Object> statementParameters = new HashMap<>();
            queryBodiesPartitionedList.forEach(query -> statementParameters.putAll(query.getStatementParameters()));

            ret.add(new Query(statementTemplate, statementParameters));
        });

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
    public static List<Query> getSetRelationshipQueries(Collection<AsgNode> nodes) {
        List<Query> ret = new ArrayList<>();

        for (AsgNode node : nodes) {
            // If the node has no related nodes, we simply skip to the next.
            if (node.getReferences().isEmpty()) {
                continue;
            }

            // Creating one query for each node-node relation.
            for (Map.Entry<AsgNode, String> relation : node.getReferences().entrySet()) {
                AsgNode referencedNode = relation.getKey();
                String referenceLabel = relation.getValue();

                Query q = createRelationQueryBetweenNodes(node, referencedNode, referenceLabel);
                ret.add(q);
            }

            // Creating one bigger query for relating a node with all its related nodes.
            // About 2x slower than the one query/node-node relation version.
//            ret.add(createRelationQueryBetweenNodeAndAllRelatedNode(node));
        }

        return ret;
    }
}
