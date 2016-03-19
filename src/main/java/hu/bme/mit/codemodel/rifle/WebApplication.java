package hu.bme.mit.codemodel.rifle;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Created by steindani on 3/2/16.
 * source: https://jersey.java.net/documentation/latest/getting-started.html
 */
public class WebApplication {
//    protected static final String DB_PATH = "/home/steindani/Downloads/neo4j-community-3.0.0-M02/data/graph.db";
//    public static GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//    public static DbServices dbServices = new DbServices(graphDatabaseService);


    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/codemodel/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in hu.bme.mit.codemodel.rifle package
        final ResourceConfig rc = new ResourceConfig().packages("hu.bme.mit.codemodel.rifle.resources");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}