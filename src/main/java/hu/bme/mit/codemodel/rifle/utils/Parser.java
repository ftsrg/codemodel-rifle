package hu.bme.mit.codemodel.rifle.utils;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.SourceSpan;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steindani on 2/26/16.
 */
public class Parser {

    public static ParseResult parseWithScope(String[] paths) throws IOException, JsError {

        final List<GlobalScope> scopes = new ArrayList<>();
        final Map<Node, Maybe<SourceSpan>> locations = new HashMap<>();

        for (String path : paths) {
            String source = FileUtils.readFileToString(new File(path));

            com.shapesecurity.shift.parser.Parser.ModuleParser parser = new com.shapesecurity.shift.parser.Parser.ModuleParser(source);
            Module module = parser.parse();
            locations.putAll(parser.locations);
            GlobalScope scope = ScopeAnalyzer.analyze(module);

            scopes.add(scope);
        }

        return new ParseResult(scopes, locations);
    }

    public static class ParseResult {
        public final List<GlobalScope> scopes;
        public final Map<Node, Maybe<SourceSpan>> locations;

        public ParseResult(List<GlobalScope> scopes, Map<Node, Maybe<SourceSpan>> locations) {
            this.scopes = scopes;
            this.locations = locations;
        }
    }

}
