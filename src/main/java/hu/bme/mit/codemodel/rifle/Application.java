/**
 * Created by steindani on 1/19/16.
 */

package hu.bme.mit.codemodel.rifle;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import com.shapesecurity.shift.scope.ScopeSerializer;
import com.shapesecurity.shift.serialization.Serializer;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) throws JsError {
        String source = "function bytes(n, maxBytes) {\n" +
                "  if (maxBytes == null) {\n" +
                "    maxBytes = Math.max(1, Math.ceil(Math.log2(n + 1) / 8));\n" +
                "  }\n" +
                "  var rv = Array(maxBytes);\n" +
                "  for (var bIndex = maxBytes - 1; bIndex >= 0; --bIndex) {\n" +
                "    rv[bIndex] = n & 0xFF;\n" +
                "    n >>>= 8;\n" +
                "  }\n" +
                "  return rv;\n" +
                "}";
        Script program = Parser.parseScript(source);

        new Application().iterate(program, "");
        // System.out.println(Serializer.serialize(program));


        // GlobalScope global = ScopeAnalyzer.analyze(program);
        //String serializeScope = new ScopeSerializer().serializeScope(global);
    }

    protected ArrayList<Object> done = new ArrayList<>();

    public void iterate(Object node, String indentation) {
        if (node == null || done.contains(node)) {
            return;
        }
        done.add(node);

        System.out.println(indentation + "--------------------------------------------------------------------------------");
        System.out.println(indentation + node.hashCode());

        // print class
        Class<?> aClass = node.getClass();
        System.out.println(indentation + aClass.getSimpleName());


        // list superclasses, interfaces
        List<Class<?>> interfaces = Arrays.asList(aClass.getInterfaces());
        System.out.println(indentation + "Interfaces: " + interfaces.stream().map(elem -> elem.getSimpleName()).collect(Collectors.toList()));

        Class<?> superclass = aClass.getSuperclass();
        while (superclass != Object.class) {
            System.out.println(indentation + superclass.getSimpleName());
            superclass = superclass.getSuperclass();
        }
        System.out.println();


        // list fields
        Arrays.asList(aClass.getFields()).forEach(
                field -> {
                    System.out.print(indentation + (Modifier.isPublic(field.getModifiers()) ? "public " : "NOT public "));
                    Class<?> type = field.getType();
                    System.out.print(indentation + type.getSimpleName() + " ");
                    System.out.println(indentation + field.getName());

                    if (type == ImmutableList.class) {
                        try {
                            ImmutableList list = (ImmutableList) field.get(node);
                            list.forEach(el -> iterate(el, indentation + "    "));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type.isEnum()) {
                        try {
                            System.out.println(indentation + "    " + field.get(node).toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type.getName().startsWith("com.shapesecurity.shift.ast")) {
                        try {
                            iterate(field.get(node), indentation + "    ");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            System.out.println(indentation + "    " + field.get(node));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        System.out.println();

        // list methods
        // System.out.println(indentation + aClass.getMethods());
    }

}
