MATCH
// exporter.js: export default function name1(foo) { return foo * 2 };
    (exporter:CompilationUnit)-[:contains]->(exporterGlobalScope:GlobalScope)-[:children]->(exporterModuleScope:Scope)
        -[:astNode]->(exporterModule:Module)-[:items]->(:ExportDefault)
        -[:body]->(exportedFunctionDeclarationToMerge:FunctionDeclaration)
        <-[:astNode]-(exportedFunctionScopeToMerge:Scope),
    (exportedFunctionDeclarationToMerge)-[:name]->(:BindingIdentifier)<-[:node]-(declarationToMerge:Declaration)
        <-[:declarations]-(exportedFunctionVariable:Variable)<--(:Map)<-[:variables]-(exporterModuleScope),

// importer.js: import { name1 } from "exporter";
    (importer:CompilationUnit)-[:contains]->(importerGlobalScope:GlobalScope)-[:children]->(importerModuleScope:Scope)
        -[:astNode]->(importerModule:Module)-[:items]->(import:ImportDeclaration)
        -[:namedImports]->(importSpecifier:ImportSpecifier)
        -[:binding]->(importBindingIdentifierToMerge:BindingIdentifier)<-[:node]-(declarationToDelete:Declaration)
        <-[:declarations]-(importedVariable:Variable)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier
    AND exportedFunctionVariable.name = importedVariable.name

CREATE UNIQUE
    (exportedFunctionDeclarationToMerge)-[:name]->(importBindingIdentifierToMerge),
    (exportedFunctionDeclarationToMerge)<-[:items]-(importerModule),
    (exportedFunctionScopeToMerge)<-[:children]-(importerModuleScope),
    (importedVariable)-[:declarations]->(declarationToMerge),
    (declarationToMerge)-[:node]->(importBindingIdentifierToMerge)

DETACH DELETE
declarationToDelete
;
