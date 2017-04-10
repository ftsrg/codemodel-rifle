```Cypher
/**
 * Initial setup after subgraph insert
 */
MATCH		// Match called FunctionDeclarations for every CallExpression
	(call:CallExpression)-[:callee]->(:IdentifierExpression)
	<--(:Either)<-[:node]-(:Reference)-[:references]-(:Variable)
	-[:declarations]->(:Declaration)-[:node]->(bindingIdentifier)
	<-[:name]-(fd:FunctionDeclaration)
MATCH          // List every call from a function body
	(fun:FunctionDeclaration)-[*]->(call:CallExpression)

MERGE		// Create a calls relationship between the caller
		// FunctionDeclaration and the called FunctionDeclaration
	(fun)-[:calls]->(fd)
```

```Cypher
/**
 * Get every FunctionDeclaration used by the export
 */
MATCH		// List the exported FunctionDeclaration and every
		// FunctionDeclaration that this may use
	(main)-[:items]->(:ExportDeclaration)-[:declaration]
	->(fd:FunctionDeclaration)-[:calls*]->(f)
RETURN
	fd, f
```

```Cypher
/**
 * Get not used FunctionDeclarations
 */
MATCH
		// Find the exported FunctionDeclaration that may be an entrance point
	(main)-[:items]->(:ExportDeclaration)-[:declaration]->(fd:FunctionDeclaration)
MATCH		// Find every FunctionDeclaration that should be available through the
		// entrance points
	(find:FunctionDeclaration)
WHERE		// List the ones that are not available (Kleene closure) from the
		// entrance nodes (thus are not the entrance nodes "<>").
	(
		NOT
		(fd)-[:calls*]->(find)
	) AND (
		find <> fd
	) AND (
		main:Script OR main:Module
	)

RETURN
	find
```
