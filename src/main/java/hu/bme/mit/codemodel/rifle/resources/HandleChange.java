package hu.bme.mit.codemodel.rifle.resources;

import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.ParserWithLocation;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.GraphIterator;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steindani on 3/23/16.
 */
@Path("handle")
public class HandleChange {

    private static final String SET_COMMIT_HASH = ResourceReader.query("setcommithash");
    private static final String REMOVE_FILE = ResourceReader.query("removefile");

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response add(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,
            String content,

            @DefaultValue("master")
            @QueryParam("branchid") String branchid,
            @QueryParam("commithash") String commithash
    ) {

        setCommitHashInNewTransaction(branchid, commithash);

        try {
            parseFile(sessionid, path, content, branchid);

            // TODO provide URI for the parsed content?
            return Response.created(URI.create("")).build();
        } catch (JsError jsError) {
            System.err.println(path);
            jsError.printStackTrace();

            final Exception exception = new Exception(path);
            exception.addSuppressed(jsError);
            return Response.serverError().entity(exception).build();
        }
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response modify(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,
            String content,

            @DefaultValue("master")
            @QueryParam("branchid") String branchid,
            @QueryParam("commithash") String commithash
    ) {
        final Response response = remove(sessionid, path, branchid, commithash);
        if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            return response;
        }
        return add(sessionid, path, content, branchid, commithash);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(
            @QueryParam("sessionid") String sessionid,
            @QueryParam("path") String path,

            @DefaultValue("master")
            @QueryParam("branchid") String branchid,
            @QueryParam("commithash") String commithash
    ) {
        final boolean result = removeFile(sessionid, path, branchid, commithash);
        if (result) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }


    protected void parseFile(String sessionid, String path, String content, String branchid) throws JsError {
        ParserWithLocation parser = new ParserWithLocation();
        Module module = parser.parseModule(content);

        GlobalScope scope = ScopeAnalyzer.analyze(module);
        GraphIterator iterator = new GraphIterator(DbServicesManager.getDbServices(branchid), path, parser);
        iterator.iterate(scope, sessionid);
    }

    protected boolean removeFile(String sessionid, String path, String branchid, String commithash) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("path", path);
        parameters.put("sessionid", sessionid);

        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        try (Transaction tx = dbServices.beginTx()) {
            setCommitHash(dbServices, tx, branchid, commithash);
            dbServices.graphDb.execute(REMOVE_FILE, parameters);
            tx.success();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void setCommitHash(DbServices dbServices, Transaction tx, String branchid, String commitHash) {
        if (commitHash == null) {
            return;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("commithash", commitHash);

        dbServices.graphDb.execute(SET_COMMIT_HASH, parameters);
    }

    private void setCommitHashInNewTransaction(String branchid, String commithash) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        try (Transaction tx = dbServices.beginTx()) {
            setCommitHash(dbServices, tx, branchid, commithash);
            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
