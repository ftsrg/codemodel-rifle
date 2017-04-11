MATCH
// exporter.js: export { name1 as default };
// Known Shift parser issue: name1 gets exported as namedExport, not as defaultExport.
// See https://github.com/FTSRG/codemodel-rifle/issues/2
    (exporter:CompilationUnit)-[:contains]->(:ExportLocals)-[:namedExports]->(exportLocalSpecifier:ExportLocalSpecifier)
        -[:name]->(:IdentifierExpression)<-[:node]-(:Reference)<-[:references]-(:Variable)
        -[:declarations]->(declarationToMerge:Declaration)
        -[:node]->(:BindingIdentifier),

// importer.js: import defaultName from "exporter";
    (importer:CompilationUnit)-[:contains]->(import:Import)
        -[:defaultBinding]->(importBindingIdentifierToMerge:BindingIdentifier)
        <-[:node]-(declarationToDelete:Declaration)<-[:declarations]-(importedVariable:Variable)<-[:defaultMember]-()

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier
    AND exportLocalSpecifier.exportedName = 'default'

CREATE UNIQUE
    (importedVariable)-[:declarations]->(declarationToMerge),
    (declarationToMerge)-[:node]->(importBindingIdentifierToMerge)

DETACH DELETE
declarationToDelete
;
