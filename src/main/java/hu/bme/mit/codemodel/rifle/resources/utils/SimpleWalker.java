package hu.bme.mit.codemodel.rifle.resources.utils;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import org.neo4j.graphdb.*;
import org.neo4j.walk.Visitor;
import org.neo4j.walk.Walker;

/**
 * Created by steindani on 7/5/16.
 */
public class SimpleWalker extends Walker {
    private final DbServices dbServices;

    public SimpleWalker(DbServices dbServices) {
        this.dbServices = dbServices;
    }

    @Override
    public <R, E extends Throwable> R accept(Visitor<R, E> visitor) throws E {
        for (Node node : dbServices.graphDb.getAllNodes()) {
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
