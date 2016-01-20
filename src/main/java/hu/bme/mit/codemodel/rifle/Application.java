/**
 * Created by steindani on 1/19/16.
 */

package hu.bme.mit.codemodel.rifle;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
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

        // System.out.println("@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
        System.out.println("@prefix id:     <http://inf.mit.bme.hu/codemodel#> .");
        new Application().iterate(program);
        // System.out.println(Serializer.serialize(program));


        // GlobalScope global = ScopeAnalyzer.analyze(program);
        //String serializeScope = new ScopeSerializer().serializeScope(global);
    }

    protected ArrayList<Object> done = new ArrayList<>();

    public void iterate(Object node) {
        if (node == null || done.contains(node)) {
            return;
        }
        done.add(node);

        System.out.println();

        // print class
        Class<?> aClass = node.getClass();
        //printType(id, aClass.getSimpleName());
        String id = String.valueOf(aClass.getSimpleName() + "_" + node.hashCode());


        // list superclasses, interfaces
//        List<Class<?>> interfaces = Arrays.asList(aClass.getInterfaces());
//        interfaces.forEach(elem -> printType(id, elem.getSimpleName()));
//
//        Class<?> superclass = aClass.getSuperclass();
//        while (superclass != Object.class) {
//            printType(id, superclass.getSimpleName());
//            superclass = superclass.getSuperclass();
//        }


        // list fields
        Arrays.asList(aClass.getFields()).forEach(
                field -> {
                    // System.out.print(indentation + (Modifier.isPublic(field.getModifiers()) ? "public " : "NOT public "));
                    Class<?> type = field.getType();
                    // System.out.print(indentation + type.getSimpleName() + " ");
                    String fieldName = field.getName();

                    if (type == ImmutableList.class) {
                        try {
                            ImmutableList list = (ImmutableList) field.get(node);
                            list.forEach(
                                    el -> {
                                        printTripleRef(id, fieldName, el.getClass().getSimpleName() + "_" + el.hashCode());
                                        iterate(el);
                                    }
                            );

                            // TODO create a list node with numbered connections
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type.isEnum()) {
                        try {
                            printTriple(id, fieldName, field.get(node).toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type.getName().startsWith("com.shapesecurity.shift.ast")) {
                        try {
                            Object el = field.get(node);
                            printTripleRef(id, fieldName, el.getClass().getSimpleName() + "_" + el.hashCode());
                            iterate(el);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type.getName().startsWith("com.shapesecurity.functional.data")) {
                        try {
                            Maybe el = (Maybe) field.get(node);
                            if (el.isJust()) {
                                Object obj = el.just();
                                printTripleRef(id, fieldName, obj.getClass().getSimpleName() + "_" + obj.hashCode());
                                iterate(obj);
                            } else {
                                printTripleRef(id, fieldName, "null");
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Object el = field.get(node);
                            printTriple(id, fieldName, el.toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    // TODO Maybe
                }
        );
        System.out.println();

        // list methods
        // System.out.println(indentation + aClass.getMethods());
    }

    protected void printType(String subject, String object) {
        System.out.println("id:" + subject.replace('-', '_') + "  a   \"" + object + "\" .");
    }

    protected void printTriple(String subject, String predicate, String object) {
        System.out.println("id:" + subject.replace('-', '_') + "  <" + predicate + ">   \"" + object + "\" .");
    }

    protected void printTripleRef(String subject, String predicate, String id) {
        System.out.println("id:" + subject.replace('-', '_') + " <" + predicate + ">   id:" + id.replace('-', '_') + " .");
    }

}
