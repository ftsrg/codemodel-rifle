/**
 * Created by steindani on 1/19/16.
 */

package hu.bme.mit.codemodel.rifle;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import com.shapesecurity.shift.scope.ScopeSerializer;
import com.shapesecurity.shift.serialization.Serializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
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

        GlobalScope global = ScopeAnalyzer.analyze(program);
        // String serializeScope = new ScopeSerializer().serializeScope(global);

        new Application().iterate(global);
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
        String id = generateId(node);


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
                            handleImmutableList(list, id, fieldName);
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
                            printTripleRef(id, fieldName, generateId(el));
                            iterate(el);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type == Maybe.class) {
                        try {
                            Maybe el = (Maybe) field.get(node);
                            if (el.isJust()) {
                                Object obj = el.just();
                                printTripleRef(id, fieldName, generateId(obj));
                                iterate(obj);
                            } else {
                                printTripleRef(id, fieldName, "null");
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type == HashTable.class) {
                        try {
                            HashTable table = (HashTable) field.get(node);
                            String tableId = generateId(table);

                            // id -- [field] -> table
                            printTripleRef(id, fieldName, tableId);

                            for (Object el: table.entries()) {
                                Pair pair = (Pair) el;

                                if (pair.b instanceof ImmutableList) {
                                    // table -- [a] -> b.*
                                    handleImmutableList((ImmutableList) pair.b, tableId, pair.a.toString());

                                    // id -- [field] -> b.*
                                    handleImmutableList((ImmutableList) pair.b, id, fieldName);
                                } else {
                                    printTripleRef(id, fieldName, generateId(pair.b));
                                    printTripleRef(tableId, pair.a.toString(), generateId(pair.b));
                                    iterate(pair.b);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (type.getName().startsWith("com.shapesecurity.functional.data")) {
                        // TODO
                    } else {
                        try {
                            Object el = field.get(node);
                            printTriple(id, fieldName, el.toString());
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

    protected void handleImmutableList(ImmutableList list, String id, String fieldName) {
        list.forEach(
                el -> {
                    printTripleRef(id, fieldName, generateId(el));
                    iterate(el);
                }
        );

        // TODO create a list node with numbered connections
    }

    @NotNull
    private String generateId(Object obj) {
        return obj.getClass().getSimpleName() + "_" + obj.hashCode();
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
