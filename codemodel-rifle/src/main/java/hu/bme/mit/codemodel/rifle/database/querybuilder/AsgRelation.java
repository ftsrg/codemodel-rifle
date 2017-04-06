package hu.bme.mit.codemodel.rifle.database.querybuilder;

public class AsgRelation {
    private final AsgNode fromNode;
    private final AsgNode toNode;
    private final String relationshipLabel;

    public AsgRelation(AsgNode fromNode, AsgNode toNode, String relationshipLabel) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.relationshipLabel = relationshipLabel;
    }

    public AsgNode getFromNode() {
        return fromNode;
    }

    public AsgNode getToNode() {
        return toNode;
    }

    public String getRelationshipLabel() {
        return relationshipLabel;
    }
}
