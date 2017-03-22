import java.io.IOException;

import org.junit.Test;

import hu.bme.mit.codemodel.rifle.actions.visualization.ExportGraph;
import hu.bme.mit.codemodel.rifle.database.DbServiceDecorator;
import neo4j.driver.testkit.EmbeddedTestkitDriver;

public class HelloTest {

    @Test
    public void helloTest() throws IOException {
        // TODO
        // run some graph building
        EmbeddedTestkitDriver driver = null;
        DbServiceDecorator dbServices = new DbServiceDecorator(driver.getUnderlyingDatabaseService());
        ExportGraph eg = new ExportGraph(dbServices );
        eg.png("master", -1, true, true);
    }

}
