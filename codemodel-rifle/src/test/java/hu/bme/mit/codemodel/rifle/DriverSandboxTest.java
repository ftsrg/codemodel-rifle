package hu.bme.mit.codemodel.rifle;

import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;

public class DriverSandboxTest {

    @Test
    public void test() {
        DbServices dbServices = DbServicesManager.getDbServices("master");

        StatementResult result = dbServices.execute("CREATE (n) RETURN n");
        Node x = result.next().get(0).asNode();
        System.out.println(x);
    }
}
