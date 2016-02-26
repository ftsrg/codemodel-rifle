package hu.bme.mit.codemodel.rifle.utils;

import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by steindani on 2/26/16.
 */
public class Parser {

    public static List<GlobalScope> parseWithScope(String[] paths) throws IOException, JsError {

        List<GlobalScope> result = new ArrayList<>();

        for (String path : paths) {
            String source = FileUtils.readFileToString(new File(path));
            Module module = com.shapesecurity.shift.parser.Parser.parseModule(source);
            GlobalScope scope = ScopeAnalyzer.analyze(module);

            result.add(scope);
        }

        return result;
    }

}
