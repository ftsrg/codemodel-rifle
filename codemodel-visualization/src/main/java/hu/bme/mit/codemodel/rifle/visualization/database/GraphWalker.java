package hu.bme.mit.codemodel.rifle.visualization.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.walk.Visitor;
import org.neo4j.walk.Walker;

import com.google.common.collect.ImmutableMap;

// based on org.neo4j.walk.Walker.crosscut()
public class GraphWalker extends Walker {

    private final List<Node> nodes = new ArrayList<>();
    private final boolean simple;
    private final boolean cfg;

    public GraphWalker(DbServiceDecorator dbServices, boolean simple, boolean cfg) {
        this.simple = simple;
        this.cfg = cfg;

        for (Node node : dbServices.getAllNodes()) {
            this.nodes.add(node);
        }
    }

    public GraphWalker(DbServiceDecorator dbServices, long rootId, boolean simple, boolean cfg) {
        this.simple = simple;
        this.cfg = cfg;

        final Node root = dbServices.getNodeById(rootId);
        nodes.add(root);

        final Result result = dbServices.execute(
                "MATCH (root)-[*]->(n) WHERE id(root) = $rootId RETURN id(n) as id",
                ImmutableMap.of("rootId", rootId)
            );

        while (result.hasNext()) {
            final Map<String, Object> next = result.next();
            nodes.add(dbServices.getNodeById(Long.valueOf(next.get("id").toString())));
        }

//            nodes = dbServices.graphDb
//                    .traversalDescription()
//                    .breadthFirst()
//                    .traverse(root)
//                    .nodes()
//                    .stream().map(node -> node).collect(Collectors.toList());
    }

    @Override
    public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
        //filternodes:
        for (Node node : nodes) {

            if (simple) {
                if (node.hasLabel(Label.label("CompilationUnit"))) {
                    continue; // filternodes;
                }
                if (node.hasLabel(Label.label("SourceSpan"))) {
                    continue; // filternodes;
                }
                if (node.hasLabel(Label.label("SourceLocation"))) {
                    continue; // filternodes;
                }
            }

            if (!cfg) {
                if (node.hasLabel(Label.label("End"))) {
                    continue; // filternodes;
                }
            }

            visitor.visitNode(node);
            for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
                if (nodes.contains(relationship.getOtherNode(node))) {
                    if (relationship.isType(RelationshipType.withName("location"))) {
                        continue;
                    }

                    if (!cfg) {
                        if (relationship.isType(RelationshipType.withName("_end"))) {
                            continue;
                        }
                        if (relationship.isType(RelationshipType.withName("_next"))) {
                            continue;
                        }
                        if (relationship.isType(RelationshipType.withName("_true"))) {
                            continue;
                        }
                        if (relationship.isType(RelationshipType.withName("_false"))) {
                            continue;
                        }
                        if (relationship.isType(RelationshipType.withName("_normal"))) {
                            continue;
                        }
                    }

                    visitor.visitRelationship(relationship);
                }
            }
        }
        return visitor.done();
    }
}
