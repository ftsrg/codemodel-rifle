MATCH
// export.js: export { name1 as exportedName1 };
    (exporter:CompilationUnit)-[:contains]->(:ExportLocals)-[:namedExports]->(exportLocalSpecifier:ExportLocalSpecifier)
        -[:name]->(:IdentifierExpression)<-[:node]-(:Reference)<-[:references]-(:Variable)
        -[:declarations]->(declarationListToMerge:List)-->(declarationToMerge:Declaration)
        -[:node]->(:BindingIdentifier),

// import.js: import { exportedName1 as name1 } from "export";
    (importer:CompilationUnit)-[:contains]->(import:Import)-[:namedImports]->(importSpecifier:ImportSpecifier)
        -[:binding]->(importBindingIdentifierToMerge:BindingIdentifier)<-[:node]-(declarationToDelete:Declaration)
        <--(declarationListToDelete:List)<-[:declarations]-(importedVariable:Variable)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier
    AND exportLocalSpecifier.exportedName = importSpecifier.name

CREATE UNIQUE
    (importedVariable)-[:declarations]->(declarationToMerge),
    (importedVariable)-[:declarations]->(declarationListToMerge),
    (declarationToMerge)-[:node]->(importBindingIdentifierToMerge)

DETACH DELETE
declarationToDelete,
declarationListToDelete
;
