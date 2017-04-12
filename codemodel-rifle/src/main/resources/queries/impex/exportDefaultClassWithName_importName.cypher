MATCH
// exporter.js: export default class name1 { … };
    (exporter:CompilationUnit)-[:contains]->(exporterGlobalScope:GlobalScope)-[:children]->(exporterModuleScope:Scope)
        -[:astNode]->(exporterModule:Module)-[:items]->(:ExportDefault)
        -[:body]->(exportedClassDeclarationToMerge:ClassDeclaration)<-[:astNode]-(exportedClassScopeToMerge:Scope),
    (exportedClassDeclarationToMerge)-[:name]->(:BindingIdentifier)<-[:node]-(declarationToMerge:Declaration)
        <-[:declarations]-(exportedClassVariable:Variable)<--(:Map)<-[:variables]-(exporterModuleScope),

// importer.js: import { name1 } from "exporter";
    (importer:CompilationUnit)-[:contains]->(importerGlobalScope:GlobalScope)-[:children]->(importerModuleScope:Scope)
        -[:astNode]->(importerModule:Module)-[:items]->(import:ImportDeclaration)
        -[:namedImports]->(importSpecifier:ImportSpecifier)
        -[:binding]->(importBindingIdentifierToMerge:BindingIdentifier)<-[:node]-(declarationToDelete:Declaration)
        <-[:declarations]-(importedVariable:Variable)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier
    AND exportedClassVariable.name = importedVariable.name

CREATE UNIQUE
    (exportedClassDeclarationToMerge)-[:name]->(importBindingIdentifierToMerge),
    (exportedClassDeclarationToMerge)<-[:items]-(importerModule),
    (exportedClassScopeToMerge)<-[:children]-(importerModuleScope),
    (importedVariable)-[:declarations]->(declarationToMerge),
    (declarationToMerge)-[:node]->(importBindingIdentifierToMerge)

DETACH DELETE
declarationToDelete
;
