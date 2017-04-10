# Import
```JavaScript
import { doStuff } from "export";

doStuff();
```

# Export
```JavaScript
export default function doStuff() {}
```

# Faulty automatic merge
![Faulty automatic merge](https://github.com/steindani/codemodel-rifle/wiki/img/merge.dot.png)

[.png](https://github.com/steindani/codemodel-rifle/wiki/img/merge.dot.png) 
[.pdf](https://github.com/steindani/codemodel-rifle/wiki/img/merge.dot.pdf) 
[.dot](https://github.com/steindani/codemodel-rifle/wiki/img/merge.dot)

# Notes
 * It's easy to see that there are 2 separate AST+Scope graphs with only one connection.
 * Both of these graphs would contain one `BindingIdentifier` node with a name of `doStuff`.
 * If these are not merged export-time (and they should not be), I only need to merge these nodes based on various rules. See:
   * [https://hacks.mozilla.org/2015/08/es6-in-depth-modules/](https://hacks.mozilla.org/2015/08/es6-in-depth-modules/)
   * [https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import)
   * [https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/export](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/export)
 * For some reason these two nodes are already merged together. I've used the same `GraphIterator` instance, so if these two were the same `Object`s in Java, they are represented by the same node in the graph. *With the static parsing function in Shift this should not happen.*

# Transformation

## Initial, separated graph
After using separating the same object references and storing them as different graph nodes, the following graph was presented:

![Initial, separated graph](https://github.com/steindani/codemodel-rifle/wiki/img/separate.dot.png)

[.png](https://github.com/steindani/codemodel-rifle/wiki/img/separate.dot.png) 
[.pdf](https://github.com/steindani/codemodel-rifle/wiki/img/separate.dot.pdf) 
[.dot](https://github.com/steindani/codemodel-rifle/wiki/img/separate.dot)

## Steps
 1. Find the imported `IdentifierExporession` in the `GlobalScope` node's `items` list.
 2. Find the connected upstream `Variable` node.
 3. Find the `Declaration` for the `Export` that has a `Node` with the same `BindingIdentifier` as the import.
 4. Connect the `Variable` on the import side to the `Declaration` on the export side with a `declarations` relationship.

## Query
```Cypher
MATCH
	(:ImportDeclaration)-[*]->(importIdentifier:BindingIdentifier)
MATCH
	(:GlobalScope)-[:through]->(:HashTable)-[]->(reference:Reference)
	-[:node]->(:Either)-[:data]->(identifier:IdentifierExpression)
MATCH
	(:ExportDeclaration)-[:declaration]->(:Node)-[:name]
	->(exportIdentifier:BindingIdentifier)

MATCH
	(exportIdentifier)<-[:node]-(declaration:Declaration)
MATCH
	(reference)<-[:references]-(variable:Variable)

WHERE
	importIdentifier.name = identifier.name AND 
	exportIdentifier.name = importIdentifier.name

CREATE UNIQUE
	(variable)-[:declarations]->(declaration)

RETURN
	importIdentifier, exportIdentifier, variable, declaration
```
