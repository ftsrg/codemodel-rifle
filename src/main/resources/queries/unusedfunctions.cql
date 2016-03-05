/**
 * Get not used FunctionDeclarations
 */
MATCH
        // Find the exported FunctionDeclaration that may be an entrance point
    (main)-[:items]->(:ExportDeclaration)-[:declaration]->(fd:FunctionDeclaration)
MATCH       // Find every FunctionDeclaration that should be available through the
        // entrance points
    (find:FunctionDeclaration)

MATCH
	(start)<-[:start]-()-[:location]-(find)-[:location]->()-[:end]->(end)

WHERE       // List the ones that are not available (Kleene closure) from the
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
    find, start.line, start.column, end.line, end.column
