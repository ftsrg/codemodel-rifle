MATCH
// exporter.js: export default function(foo) { return foo * 2 };
    (exporter:CompilationUnit)-[:contains]->(exporterGlobalScope:GlobalScope)-[:children]->(exporterModuleScope:Scope)
        -[:astNode]->(exporterModule:Module)-[:items]->(:ExportDefault)
        -[:body]->(exportedFunctionDeclarationToMerge:FunctionDeclaration)
        <-[:astNode]-(exportedFunctionScopeToMerge:Scope),
    (exportedFunctionDeclarationToMerge)-[:name]->(:BindingIdentifier)<-[:node]-(declarationToMerge:Declaration)
        <-[:declarations]-(:Variable)<--(:Map)<-[:variables]-(exporterModuleScope),

// importer.js: import defaultName from "exporter";
    (importer:CompilationUnit)-[:contains]->(importerGlobalScope:GlobalScope)-[:children]->(importerModuleScope:Scope)
        -[:astNode]->(importerModule:Module)-[:items]->(import:Import)
        -[:defaultBinding]->(importBindingIdentifierToMerge:BindingIdentifier)
        <-[:node]-(declarationToDelete:Declaration)<-[:declarations]-(importedVariable:Variable)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier

CREATE UNIQUE
    (exportedFunctionDeclarationToMerge)-[:name]->(importBindingIdentifierToMerge),
    (exportedFunctionDeclarationToMerge)<-[:items]-(importerModule),
    (exportedFunctionScopeToMerge)<-[:children]-(importerModuleScope),
    (importedVariable)-[:declarations]->(declarationToMerge),
    (declarationToMerge)-[:node]->(importBindingIdentifierToMerge)

DETACH DELETE
declarationToDelete
;
