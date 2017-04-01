package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.HashMap;
import java.util.Map;

public class Query {
    private String statementTemplate;
    private Map<String, Object> statementParameters = new HashMap<>();

    public Query() {}

    public Query(String template, Map<String, Object> parameters) {
        this.statementTemplate = template;
        this.statementParameters = parameters;
    }

    public String getStatementTemplate() {
        return statementTemplate;
    }

    public Map<String, Object> getStatementParameters() {
        return statementParameters;
    }

    public synchronized void append(String template, Map<String, Object> parameters) {
        StringBuilder builder = new StringBuilder(this.statementTemplate);
        builder.append(template);
        this.statementTemplate = builder.toString();
        this.statementParameters.putAll(parameters);
    }
}
