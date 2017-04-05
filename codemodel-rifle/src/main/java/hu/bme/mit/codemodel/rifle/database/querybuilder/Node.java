package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.*;

/**
 * Our Node business entity.
 */
public class Node {
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
    private Map<Node, String> references = new HashMap<>();

    /**
     * Default constructor.
     */
    public Node() {
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
    public void addReference(Node node, String label) {
        this.references.put(node, label);
    }

    /**
     * Getter for references.
     *
     * @return Map
     */
    public Map<Node, String> getReferences() {
        return this.references;
    }
}
