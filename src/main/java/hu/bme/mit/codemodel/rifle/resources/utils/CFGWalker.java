package hu.bme.mit.codemodel.rifle.resources.utils;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.walk.Visitor;
import org.neo4j.walk.Walker;

import java.util.*;
import java.util.stream.Collectors;

// based on org.neo4j.walk.Walker.crosscut()
public class CFGWalker extends Walker {

    private final Set<Node> nodes = new HashSet<>();
    List<String> relationships = Arrays.asList(":`_end`", ":`_normal`", ":`_next`", ":`_true`", ":`_false`");
    List<String> relationshipLabels = relationships.stream()
            .map(before -> before.substring(2, before.length() - 1))
            .collect(Collectors.toList());

    public CFGWalker(DbServices dbServices) {
        final Result result = dbServices.graphDb.execute(
                "MATCH (a)-[" + String.join("|", relationships) + "]->(b) RETURN id(a) as a, id(b) as b"
        );

        while (result.hasNext()) {
            final Map<String, Object> next = result.next();
            nodes.add(dbServices.graphDb.getNodeById(Long.valueOf(next.get("a").toString())));
            nodes.add(dbServices.graphDb.getNodeById(Long.valueOf(next.get("b").toString())));
        }
    }

    @Override
    public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
        //filternodes:
        for (Node node : nodes) {
            visitor.visitNode(node);
            for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
                if (nodes.contains(relationship.getOtherNode(node))) {

                    if (!relationshipLabels.contains(relationship.getType().name())) {
                        continue;
                    }

                    visitor.visitRelationship(relationship);
                }
            }
        }
        return visitor.done();
    }
}
