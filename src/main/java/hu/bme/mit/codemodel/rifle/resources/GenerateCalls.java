package hu.bme.mit.codemodel.rifle.resources;

import hu.bme.mit.codemodel.rifle.WebApplication;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.apache.commons.io.IOUtils;
import org.neo4j.graphdb.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by steindani on 3/2/16.
 */
@Path("generatecalls")
public class GenerateCalls {
    protected static final String NAME = "generatecalls";
    protected final String query = ResourceReader.query(NAME);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateCalls() {

        Result result = WebApplication.graphDatabaseService.execute(query);
        return Response.ok(result.resultAsString()).build();

    }
}
