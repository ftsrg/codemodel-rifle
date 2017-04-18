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

    @Override
    public boolean equals(Object relation) {
        if (!(relation instanceof AsgRelation)) {
            return false;
        }

        AsgRelation relationTypeRelation = (AsgRelation)relation;

        return
            this.getFromNode().equals(relationTypeRelation.getFromNode()) &&
                this.getToNode().equals(relationTypeRelation.getToNode()) &&
                this.getRelationshipLabel().equals(relationTypeRelation.getRelationshipLabel());
    }
}
