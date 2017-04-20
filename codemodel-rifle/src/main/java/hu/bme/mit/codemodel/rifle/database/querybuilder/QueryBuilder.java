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
    private static int createThisManyAsgNodeWithOneQuery = 20;
    private static int setThisManyAsgRelationsWithOneQuery = 1;

    /**
     * Creates a new query body for creating a node.
     *
     * @param node
     * @return Query
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
     * Create a query body with parameters for a MATCH.
     * <p>
     * Like:
     * (`node` {`id`: $`idBindingIdentifier`})
     *
     * @param node
     * @return Query
     */
    private static Query createMatchNodeQueryBody(AsgNode node) {
        String nodeIdBinding = Utils.createUniqueIdentifierName();
        String statementTemplate = String.format("(`%s`:`AsgNode`{`id`:$`%s`})", node.getId(), nodeIdBinding);
        Map<String, Object> statementParameters = new HashMap<>();
        statementParameters.put(nodeIdBinding, node.getId());

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * Creates a query body for a previously relation with previously matched nodes:
     * (`nodeFromIdentifier`)-[:`relationshipLabel`]->(`nodeToIdentifier`)
     *
     * @param relation
     * @return Query
     */
    private static Query createCreateRelationshipQueryBody(AsgRelation relation) {
        String relationshipTemplate = String.format("(`%s`)-[:`%s`]->(`%s`)", relation.getFromNode().getId(),
            relation.getRelationshipLabel(), relation.getToNode().getId());
        Map<String, Object> relationshipParameters = new HashMap<>();

        return new Query(relationshipTemplate, relationshipParameters);
    }

    /**
     * We assemble the create queries for a list of nodes.
     * <p>
     * We create a query which creates the given nodes in the following form:
     * CREATE (`identifier`:`label1`:`label2`...{`prop1`:'{`prop1ParamBinding`}',`prop2`:'{`prop2ParamBinding`}'}), etc.
     *
     * @return Query
     */
    private static Query createCreateNodesQuery(AsgNode node) {
        Query queryBody = createCreateNodeQueryBody(node);

        String statementTemplate = "CREATE" + queryBody.getStatementTemplate();
        Map<String, Object> statementParameters = queryBody.getStatementParameters();

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
    private static Query createCreateNodesQuery(List<AsgNode> nodes) {
        // If nodes only contains one element, go the faster way.
        if (nodes.size() == 1) {
            return createCreateNodesQuery(nodes.get(0));
        }

        List<Query> queryBodies = new ArrayList<>();
        nodes.forEach(node -> queryBodies.add(createCreateNodeQueryBody(node)));

        String statementTemplate = "CREATE" + String.join(",", queryBodies.stream().map(Query::getStatementTemplate)
            .toArray(String[]::new));
        Map<String, Object> statementParameters = new HashMap<>();
        queryBodies.forEach(queryBody -> statementParameters.putAll(queryBody.getStatementParameters()));

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * Creates a relationship query.
     * <p>
     * MATCH (`from` {`id`: $`fromId`}), (`to`: {`id`: $`toId`})
     * CREATE (`from`)-[:`relationshipLabel`]->(`to`)
     *
     * @param relation
     * @return Query
     */
    private static Query createSetRelationshipQuery(AsgRelation relation) {
        String statementTemplate = String.format("MATCH(`from`:`AsgNode`{`id`:$`fromId`}),(`to`:`AsgNode`{`id`:$`toId`})CREATE(`from`)" +
            "-[:`%s`]->(`to`)", relation.getRelationshipLabel());

        Map<String, Object> statementParameters = new HashMap<>();
        statementParameters.put("fromId", relation.getFromNode().getId());
        statementParameters.put("toId", relation.getToNode().getId());

        return new Query(statementTemplate, statementParameters);
    }

    /**
     * Create a relationship setting query from the provided relations.
     * <p>
     * First matches all necessary nodes, then creates the relationships.
     * All in one turn.
     *
     * @param relations
     * @return Query
     */
    private static Query createSetRelationshipQuery(List<AsgRelation> relations) {
        // If nodes contains only one element, go the faster way.
        if (relations.size() == 1) {
            return createSetRelationshipQuery(relations.get(0));
        }

        StringBuilder statementTemplate = new StringBuilder();
        Map<String, Object> statementParameters = new HashMap<>();

        // All relations' "from" and "to" nodes should be matched.
        // We create a set in which we store the nodes we will create a MATCH query for.
        Set<AsgNode> matchedNodes = new HashSet<>();
        // Adding "to" nodes of the relations.
        matchedNodes.addAll(relations.stream().map(AsgRelation::getToNode).collect(Collectors.toSet()));
        // Adding "from" nodes of the relations.
        matchedNodes.addAll(relations.stream().map(AsgRelation::getFromNode).collect(Collectors.toSet()));

        // We assemble the bodies of the MATCH query of all nodes that should be matched for creating the relation.
        List<Query> matchQueryBodies = new ArrayList<>();
        matchedNodes.forEach(node -> matchQueryBodies.add(createMatchNodeQueryBody(node)));

        // We assemble the bodies of the CREATE relationship queries of all nodes that was matched previously.
        List<Query> createRelationQueryBodies = new ArrayList<>();
        relations.forEach(relation -> createRelationQueryBodies.add(createCreateRelationshipQueryBody(relation)));

        // We assemble the match query itself, handing over the parameters.
        statementTemplate.append("MATCH");
        statementTemplate.append(matchQueryBodies.stream().map(Query::getStatementTemplate)
            .collect(Collectors.joining(",")));
        matchQueryBodies.forEach(queryBody -> statementParameters.putAll(queryBody.getStatementParameters()));

        // Then we assemble the relationship creating query parts with the previously matched nodes.
        statementTemplate.append("CREATE");
        statementTemplate.append(createRelationQueryBodies.stream().map(Query::getStatementTemplate)
            .collect(Collectors.joining(",")));
        createRelationQueryBodies.forEach(queryBody -> statementParameters.putAll(queryBody.getStatementParameters()));

        return new Query(statementTemplate.toString(), statementParameters);
    }

    /**
     * The query builder has two basic jobs: the first is to create queries
     * to create the nodes with the provided labels and properties.
     * <p>
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
        Lists.partition(nodes, createThisManyAsgNodeWithOneQuery)
            .forEach(fewerNodes -> ret.add(createCreateNodesQuery(fewerNodes)));

        return ret;
    }

    /**
     * The query builder has two basic jobs: the second if to create queries
     * to set the relationships between nodes.
     * <p>
     * This returns a list of queries: each one can be executed without further modifications.
     * Multiple relationship setting queries are merged into fewer queries configured above.
     *
     * @param nodes
     * @return List
     */
    public static List<Query> getSetRelationshipQueries(List<AsgNode> nodes) {
        List<Query> ret = new ArrayList<>();

        // Collecting all node-node relations into a list.
        List<AsgRelation> relations = new ArrayList<>();
        nodes.forEach(node -> relations.addAll(node.getRelations()));

        // Setting multiple relationships in one query.
        Lists.partition(relations, setThisManyAsgRelationsWithOneQuery)
            .forEach(fewerRelations -> ret.add(createSetRelationshipQuery(fewerRelations)));

        return ret;
    }
}
