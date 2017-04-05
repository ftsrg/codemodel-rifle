package hu.bme.mit.codemodel.rifle.database.querybuilder;

import java.util.UUID;

public class Utils {
    /**
     * Creates a unique node identifier.
     *
     * @return String
     */
    public static String createUniqueIdentifierName() {
        return String.format("_%s", UUID.randomUUID().toString().replace("-", ""));
    }
}
