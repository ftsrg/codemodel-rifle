package hu.bme.mit.codemodel.rifle.resources;

import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.Scope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.GraphIterator;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by steindani on 3/13/16.
 */
@Path("parsefile")
public class ParseFile {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response parseFile(
            @QueryParam("sessionId") String sessionId,
            @QueryParam("fileUri") String uri,
            @QueryParam("content") String content,

            @DefaultValue("master")
            @QueryParam("branchId") String branchId,

            @DefaultValue("true")
            @QueryParam("temporal") boolean temporal
    ) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        Transaction tx = dbServices.getTransactionFor(sessionId);
        if (temporal) {
            tx.failure();
        }

        try {
            Parser.ModuleParser parser = new Parser.ModuleParser(content);
            Module module = parser.parse();
            GlobalScope scope = ScopeAnalyzer.analyze(module);

            // TODO uri to relative in the caller
            GraphIterator iterator = new GraphIterator(dbServices, tx, uri, parser.locations);
            iterator.iterate(null, null, scope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.ok().build();
    }
}
