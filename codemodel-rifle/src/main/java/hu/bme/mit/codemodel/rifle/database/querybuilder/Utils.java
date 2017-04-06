package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.UUID;

/**
 * Utilities and helper functions like identifier creating, naming, etc.
 */
public class Utils {
    /**
     * Creates a unique node identifier for identifying mainly Neo4j nodes.
     *
     * @return String
     */
    public static String createUniqueIdentifierName() {
        return String.format("_%s", UUID.randomUUID().toString().replace("-", ""));
    }
}
