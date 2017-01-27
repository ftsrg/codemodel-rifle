package hu.bme.mit.codemodel.rifle;

import java.io.IOException;
import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import hu.bme.mit.codemodel.rifle.resources.imports.BuildCallGraph;
import hu.bme.mit.codemodel.rifle.resources.imports.HandleChange;
import hu.bme.mit.codemodel.rifle.resources.imports.ImportDirectory;
import hu.bme.mit.codemodel.rifle.resources.imports.ImportExport;
import hu.bme.mit.codemodel.rifle.resources.queries.RunQuery;
import hu.bme.mit.codemodel.rifle.resources.queries.TypeInference;
import hu.bme.mit.codemodel.rifle.resources.queries.UnusedFunctions;

// based on: https://jersey.java.net/documentation/latest/getting-started.html
public class WebApplication {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/codemodel/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

        // source: http://stackoverflow.com/questions/21329733/grizzly-standalone-logging
        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.FINE);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        l.addHandler(ch);

        // source: http://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger
        Logger logger = Logger.getLogger("codemodel");
        FileHandler fh;

        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("performance-metrics.txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        // create a resource config that scans for JAX-RS resources and providers
        // in hu.bme.mit.codemodel.rifle package
        final ResourceConfig rc = new ResourceConfig().packages("hu.bme.mit.codemodel.rifle.resources");
        rc.register(BuildCallGraph.class);
//        rc.register(ExportGraph.class);
//        rc.register(GetLastCommitHash.class);
        rc.register(HandleChange.class);
        rc.register(ImportDirectory.class);
        rc.register(RunQuery.class);
        rc.register(TypeInference.class);
        rc.register(UnusedFunctions.class);
        rc.register(ImportExport.class);

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
//        new JHades().overlappingJarsReport();

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}
