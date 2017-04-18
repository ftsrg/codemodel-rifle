package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.*;

/**
 * Our AsgNode business entity.
 */
public class AsgNode {
    /**
     * The ID of the node which we reference the node.
     */
    private final String id = Utils.createUniqueIdentifierName();

    /**
     * The properties of the node stored in a map (propertyName, propertyValue).
     */
    private Map<String, Object> properties = new HashMap<>();

    /**
     * The labels stored as a simple list.
     */
    private List<String> labels = new ArrayList<>();

    /**
     * References with reference labels.
     */
    private List<AsgRelation> relations = new ArrayList<>();

    /**
     * Default constructor.
     */
    public AsgNode() {
        this.addProperty("id", this.id);
    }

    /**
     * Getter for the ID.
     *
     * @return String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Two nodes are considered the same if their ID is equal.
     *
     * @param node
     * @return
     */
    public boolean equals(AsgNode node) {
        return this.getId().equals(node.getId());
    }

    /**
     * Adds a property to the node entity.
     *
     * @param name
     * @param value
     */
    public void addProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    /**
     * Getter for the properties.
     *
     * @return Map
     */
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * Adds a label to the node entity.
     *
     * @param label
     */
    public void addLabel(String label) {
        this.labels.add(label);
    }

    /**
     * Getter for the labels.
     *
     * @return List
     */
    public List<String> getLabels() {
        return this.labels;
    }

    /**
     * Adds an outwards reference to this node.
     *
     * @param node
     * @param label
     */
    public void addRelation(AsgNode node, String label) {
        this.relations.add(new AsgRelation(this, node, label));
    }

    /**
     * Getter for relations.
     *
     * @return Map
     */
    public List<AsgRelation> getRelations() {
        return this.relations;
    }

    /**
     * Tells if the node has a particular relation.
     * @see AsgRelation#equals(Object)
     *
     * @param relation
     * @return
     */
    public boolean hasRelation(AsgRelation relation) {
        return this.getRelations().contains(relation);
    }

    /**
     * Tells if the node has a particular relation.
     *
     * @param toNode
     * @param relationshipLabel
     * @return
     */
    public boolean hasRelation(AsgNode toNode, String relationshipLabel) {
        return this.getRelations().contains(new AsgRelation(this, toNode, relationshipLabel));
    }
}
