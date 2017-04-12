MATCH
// exporter.js: export default function (foo) { return foo * 2; };
    (exporter:CompilationUnit)-[:contains]->(exporterGlobalScope:GlobalScope)-[:children]->(exporterModuleScope:Scope)
        -[:astNode]->(exporterModule:Module)-[:items]->(:ExportDefault)
        -[:body]->(exportedFunctionDeclarationToMerge:FunctionDeclaration)<-[:astNode]-(exportedFunctionScopeToMerge:Scope),
    (exportedFunctionDeclarationToMerge)-[:name]->(exportBindingIdentifierToDelete:BindingIdentifier),

// importer.js: import defaultName from "exporter";
    (importer:CompilationUnit)-[:contains]->(importerGlobalScope:GlobalScope)-[:children]->(importerModuleScope:Scope)
        -[:astNode]->(importerModule:Module)-[:items]->(import:ImportDeclaration)
        -[:defaultBinding]->(importBindingIdentifierToMerge:BindingIdentifier)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier
    // Shift Parser parses default exported function with a binding named '*default*'
    AND exportBindingIdentifierToDelete.name = '*default*'

CREATE UNIQUE
    (exportedFunctionDeclarationToMerge)-[:name]->(importBindingIdentifierToMerge),
    (exportedFunctionDeclarationToMerge)<-[:items]-(importerModule),
    (exportedFunctionScopeToMerge)<-[:children]-(importerModuleScope)

DETACH DELETE
exportBindingIdentifierToDelete
;
