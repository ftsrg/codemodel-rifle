package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Query {
    /**
     * Statement template which holds the parameter bindings.
     *
     * See Neo4j StatementRunner.
     */
    private String statementTemplate;

    /**
     * Statement parameters as a Map.
     *
     * See Neo4j StatementRunner.
     */
    private Map<String, Object> statementParameters = new HashMap<>();

    public Query(String template, Map<String, Object> parameters) {
        this.statementTemplate = template;
        this.statementParameters = parameters;
    }

    /**
     * @return String
     */
    public String getStatementTemplate() {
        return statementTemplate;
    }

    /**
     * @return Map
     */
    public Map<String, Object> getStatementParameters() {
        return statementParameters;
    }

    /**
     * @return String
     */
    public String toString() {
        return this.statementTemplate;
    }
}
