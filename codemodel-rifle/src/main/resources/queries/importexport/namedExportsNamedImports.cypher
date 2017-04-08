MATCH
    (exporter:CompilationUnit)-[:contains]->(:ExportDeclaration)-[:namedExports]->(exportSpecifier:ExportSpecifier),
    (exporter)-[:contains]->(exportedVariable:Variable)-[:declarations]->(declarationToMerge:Declaration)
        -[:node]->(exportBindingIdentifierToMerge:BindingIdentifier),
    (exportedVariable)-[:declarations]->(declarationListToMerge:List),

    (importer:CompilationUnit)-[:contains]->(importDeclaration:ImportDeclaration)
        -[:namedImports]->(importSpecifier:ImportSpecifier)
        -[:binding]->(importBindingIdentifierToDelete:BindingIdentifier),
    (importer)-[:contains]->(importedVariable:Variable),

    (importedVariable)-[:declarations]->(declarationToDelete:Declaration),
    (importedVariable)-[:declarations]->(declarationListToDelete:List)

    WHERE
    exporter.parsedFilePath CONTAINS importDeclaration.moduleSpecifier
    AND exportSpecifier.exportedName = exportedVariable.name = importBindingIdentifierToDelete.name = importedVariable.
        name = exportBindingIdentifierToMerge.name

CREATE UNIQUE
    (importedVariable)-[:declarations]->(declarationToMerge),
    (importedVariable)-[:declarations]->(declarationListToMerge),
    (importSpecifier)-[:binding]->(exportBindingIdentifierToMerge)

DETACH DELETE declarationToDelete, declarationListToDelete, importBindingIdentifierToDelete
;
