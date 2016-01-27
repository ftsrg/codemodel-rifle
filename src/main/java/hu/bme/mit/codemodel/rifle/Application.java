/**
 * Created by steindani on 1/19/16.
 */

package hu.bme.mit.codemodel.rifle;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.scope.GlobalScope;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) throws JsError {
        String source = "function x() {\n" +
                "  return 2;\n" +
                "}\n" +
                "\n" +
                "function y() {\n" +
                "  return 3;\n" +
                "}\n" +
                "\n" +
                "function z() {\n" +
                "  x();\n" +
                "  y();\n" +
                "}\n" +
                "\n" +
                "z();";
        Script program = Parser.parseScript(source);

        // System.out.println("@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
        System.out.println("@prefix id:     <http://inf.mit.bme.hu/codemodel#> .");

        GlobalScope global = ScopeAnalyzer.analyze(program);
        // String serializeScope = new ScopeSerializer().serializeScope(global);

        new Application().iterate(null, null, global);
    }

    protected ArrayList<Object> done = new ArrayList<>();

    public void iterate(String parentId, String predicate, Object node) {
        if (node == null) {
            return;
        }

        Class<?> nodeType = node.getClass();
        String nodeId = generateId(node);

        if (parentId != null) {
            if (node.getClass().getName().startsWith("com.shapesecurity") || (node instanceof Iterable) || (node instanceof HashMap)) {
                printTripleRef(parentId, predicate, nodeId);
            } else {
                printTriple(parentId, predicate, node.toString());
                return;
            }
        }


        if (done.contains(node)) {
            return;
        }
        done.add(node);


//        printType(nodeId, aClass.getSimpleName());
//        // list superclasses, interfaces
//        List<Class<?>> interfaces = Arrays.asList(aClass.getInterfaces());
//        interfaces.forEach(elem -> printType(nodeId, elem.getSimpleName()));
//
//        Class<?> superclass = aClass.getSuperclass();
//        while (superclass != Object.class) {
//            printType(nodeId, superclass.getSimpleName());
//            superclass = superclass.getSuperclass();
//        }

        getAllFields(nodeType).forEach(
                field -> {
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    String fieldName = field.getName();


                    try {
                        Object o = field.get(node);
                        if (o instanceof ImmutableList) {

                            // connect the children directly
                            ((ImmutableList) o).forEach(el -> iterate(nodeId, fieldName, el));

                        } else if (o instanceof Map) {

                            Map map = (Map) o;
                            String mapId = generateId(map);

                            // id -- [field] -> table
                            printTripleRef(nodeId, fieldName, mapId);

                            for (Object el : map.entrySet()) {
                                Map.Entry entry = (Map.Entry) el;
                                iterate(mapId, entry.getKey().toString(), entry.getValue());
                            }


                        } else if (o instanceof HashTable) {

                            HashTable table = (HashTable) o;
                            String tableId = generateId(table);

                            // id -- [field] -> table
                            printTripleRef(nodeId, fieldName, tableId);

                            for (Object el : table.entries()) {
                                Pair pair = (Pair) el;
                                iterate(tableId, pair.a.toString(), pair.b);
                            }

                        } else if (o instanceof ConcatList) {

                            // connect the children directly
                            ((ConcatList) o).forEach(el -> iterate(nodeId, fieldName, el));

                        } else if (o instanceof Maybe) {

                            Maybe el = (Maybe) o;
                            if (el.isJust()) {
                                iterate(nodeId, fieldName, el.just());
                            } else {
                                iterate(nodeId, fieldName, "null");
                            }

                        } else if (o instanceof Either) {
                            // TODO
                            iterate(nodeId, fieldName, o);

                        } else if (fieldType.isEnum()) {

                            iterate(nodeId, fieldName, o.toString());

                        } else if (fieldType.getName().startsWith("com.shapesecurity.shift.ast")) {

                            iterate(nodeId, fieldName, o);

                        } else if (fieldType.getName().startsWith("com.shapesecurity.functional")) {

                            // TODO
                            iterate(nodeId, fieldName, o);

                        } else {

                            // TODO
                            iterate(nodeId, fieldName, o);

                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
//
//        List<Method> probablyGetters = Arrays.asList(nodeType.getMethods()).stream().filter(
//                method -> {
//                    return (
//                            true &&
//                                    method.getParameterCount() == 0 &&
//                                    method.getDeclaringClass().getName().startsWith("com.shapesecurity") &&
//                                    !method.getName().endsWith("hashCode")
//
//                    );
//                }
//        ).collect(Collectors.toList());
//
//        System.out.println(probablyGetters);

    }


    // http://stackoverflow.com/questions/3567372/access-to-private-inherited-fields-via-reflection-in-java
    protected List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<Field>();

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }

        return fields;
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
