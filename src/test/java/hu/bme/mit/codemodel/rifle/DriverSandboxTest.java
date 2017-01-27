package hu.bme.mit.codemodel.rifle;

import org.junit.Test;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Node;

public class DriverSandboxTest {

    @Test
    public void test() {
        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "admin"));
        Session session = driver.session();

        try (Transaction transaction = session.beginTransaction()) {
            StatementResult result = transaction.run("CREATE (n) RETURN n");
            Node x = result.next().get(0).asNode();
            System.out.println(x);
        }
    }
}
