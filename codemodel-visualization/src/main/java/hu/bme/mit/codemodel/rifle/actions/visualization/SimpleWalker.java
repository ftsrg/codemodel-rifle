package hu.bme.mit.codemodel.rifle.actions.visualization;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.walk.Visitor;
import org.neo4j.walk.Walker;

import hu.bme.mit.codemodel.rifle.database.DbServices;

public class SimpleWalker extends Walker {
    private final DbServices dbServices;

    public SimpleWalker(DbServices dbServices) {
        this.dbServices = dbServices;
    }

    @Override
    public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
        for (Node node : dbServices.getAllNodes()) {
            if (node.hasLabel(Label.label("CompilationUnit"))) {
                continue;
            }
            if (node.hasLabel(Label.label("SourceSpan"))) {
                continue;
            }
            if (node.hasLabel(Label.label("SourceLocation"))) {
                continue;
            }

            visitor.visitNode(node);
            for (Relationship edge : node.getRelationships(Direction.OUTGOING)) {
                if (edge.isType(RelationshipType.withName("location"))) {
                    continue;
                }
                visitor.visitRelationship(edge);
            }
        }
        return visitor.done();
    }
}
