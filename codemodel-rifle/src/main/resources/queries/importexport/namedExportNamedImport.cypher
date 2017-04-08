MATCH
// export.js: export { name1 };
    (exporter:CompilationUnit)-[:contains]->(:ExportDeclaration)-[:namedExports]->(exportSpecifier:ExportSpecifier),
    (exporter)-[:contains]->(exportedVariable:Variable)-[:declarations]->(declarationToMerge:Declaration)
        -[:node]->(exportBindingIdentifierToMerge:BindingIdentifier),
    (exportedVariable)-[:declarations]->(declarationListToMerge:List),

// import.js: import { name1 } from "export";
    (importer:CompilationUnit)-[:contains]->(importDeclaration:ImportDeclaration)
        -[:namedImports]->(importSpecifier:ImportSpecifier)
        -[:binding]->(importBindingIdentifierToDelete:BindingIdentifier),
    (importer)-[:contains]->(importedVariable:Variable),

    (importedVariable)-[:declarations]->(declarationToDelete:Declaration),
    (importedVariable)-[:declarations]->(declarationListToDelete:List)

    WHERE
    exporter.parsedFilePath CONTAINS importDeclaration.moduleSpecifier
    AND exportSpecifier.exportedName = exportedVariable.name
    AND exportSpecifier.exportedName = importBindingIdentifierToDelete.name
    AND exportSpecifier.exportedName = importedVariable.name
    AND exportSpecifier.exportedName = exportBindingIdentifierToMerge.name

CREATE UNIQUE
    (importedVariable)-[:declarations]->(declarationToMerge),
    (importedVariable)-[:declarations]->(declarationListToMerge),
    (importSpecifier)-[:binding]->(exportBindingIdentifierToMerge)

DETACH DELETE
declarationToDelete,
declarationListToDelete,
importBindingIdentifierToDelete
;
