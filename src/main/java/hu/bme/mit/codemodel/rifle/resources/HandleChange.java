package hu.bme.mit.codemodel.rifle.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by steindani on 3/23/16.
 */
@Path("handle")
public class HandleChange {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("add")
    public Response add(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,
            @QueryParam("content") String content
    ) {
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("modify")
    public Response modify(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,
            @QueryParam("content") String content
    ) {
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("remove")
    public Response remove(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,
            @QueryParam("content") String content
    ) {
        return Response.ok().build();
    }

}
