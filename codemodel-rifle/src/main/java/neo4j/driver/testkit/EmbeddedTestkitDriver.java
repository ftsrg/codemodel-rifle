package neo4j.driver.testkit;

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

public class EmbeddedTestkitDriver implements Driver {

    protected final GraphDatabaseService gds;
    protected final Driver driver;

    public EmbeddedTestkitDriver() {
        GraphDatabaseSettings.BoltConnector bolt = GraphDatabaseSettings.boltConnector( "0" );

        String host = "localhost";
        int port = 7687; // this is the default Neo4j port - we start one higher than this
        GraphDatabaseService graphDb = null;
        String address = null;
        while (port < 65000) {
            port++;
            address = String.format("%s:%d", host, port);
            try {
                graphDb = new TestGraphDatabaseFactory()
                    .newImpermanentDatabaseBuilder()
                    .setConfig(bolt.type, "BOLT")
                    .setConfig(bolt.enabled, "true")
                    .setConfig(bolt.address, address)
                    .newGraphDatabase();
            } catch (RuntimeException e) {
                // this is usually a org.neo4j.kernel.lifecycle.LifecycleException
                // caused by org.neo4j.helpers.PortBindException
                e.printStackTrace();
                System.out.println("Cannot connect on port " + port + ", retrying on a higher port.");
                continue;
            }
            break;
        }

        gds = graphDb;
        driver = GraphDatabase.driver("bolt://" + address);
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public Session session() {
        return driver.session();
    }

    @Override
    public Session session(AccessMode mode) {
        return driver.session(mode);
    }

    @Override
    public void close() {
        gds.shutdown();
    }

    @Override
    public Session session(String bookmark) {
        throw new UnsupportedOperationException("Bookmarks not supported.");
    }

    @Override
    public Session session(AccessMode mode, String bookmark) {
        throw new UnsupportedOperationException("Bookmarks not supported.");
    }

    @Override
    public Session session( Iterable<String> bookmarks )
    {
        throw new UnsupportedOperationException("Bookmarks not supported.");
    }

    @Override
    public Session session( AccessMode mode, Iterable<String> bookmarks )
    {
        throw new UnsupportedOperationException("Bookmarks not supported.");
    }

    public GraphDatabaseService getUnderlyingDatabaseService() {
        return gds;
    }


}
