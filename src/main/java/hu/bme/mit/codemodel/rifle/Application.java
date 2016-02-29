/**
 * Created by steindani on 1/19/16.
 */

package hu.bme.mit.codemodel.rifle;

import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.Scope;
import hu.bme.mit.codemodel.rifle.utils.DbServices;
import hu.bme.mit.codemodel.rifle.utils.GraphIterator;
import hu.bme.mit.codemodel.rifle.utils.Parser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.util.List;

public class Application {

    public static void main(String[] args) throws JsError, IOException {
        final String DB_PATH = "/home/steindani/Downloads/neo4j-community-3.0.0-M02/data/graph.db";
        final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        DbServices dbServices = new DbServices(graphDb);

        dbServices.clean();

        String[] sources = {"import.js", "export.js"};
        List<GlobalScope> scopes = Parser.parseWithScope(sources);

        GraphIterator iterator;
        for (Scope scope : scopes) {
            iterator = new GraphIterator(dbServices);
            iterator.iterate(null, null, scope);
        }

        dbServices.export("result.dot");
    }
}
