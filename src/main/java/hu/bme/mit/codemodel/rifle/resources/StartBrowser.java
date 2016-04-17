package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;

/**
 * Created by steindani on 3/2/16.
 */
@Path("browser")
public class StartBrowser {

    protected static HashMap<String, WrappingNeoServerBootstrapper> servers = new HashMap<>();

    @GET
    @Path("start")
    public Response start(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        if (servers.containsKey(branchid)) {
            return Response.ok().build();
        }

        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        WrappingNeoServerBootstrapper neoServerBootstrapper;

        try {
            GraphDatabaseAPI api = (GraphDatabaseAPI) dbServices;

            ServerConfigurator config = new ServerConfigurator(api);
            config.configuration()
                    .addProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "127.0.0.1");
            config.configuration()
                    .addProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, "7575");

            neoServerBootstrapper = new WrappingNeoServerBootstrapper(api, config);
            servers.put(branchid, neoServerBootstrapper);
            neoServerBootstrapper.start();
        } catch (Exception e) {
            //handle appropriately
            e.printStackTrace();
        }

        return Response.ok().build();
    }

    @GET
    @Path("stop")
    public Response stop(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        if (servers.containsKey(branchid)) {
            servers.get(branchid).stop();
            servers.remove(branchid);

            return Response.ok().build();
        }

        return Response.serverError().build();
    }


}
