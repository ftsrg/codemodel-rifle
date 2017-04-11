MATCH
// exporter.js: export default class { … };
    (exporter:CompilationUnit)-[:contains]->(exporterGlobalScope:GlobalScope)-[:children]->(exporterModuleScope:Scope)
        -[:astNode]->(exporterModule:Module)-[:items]->(:ExportDefault)
        -[:body]->(exportedClassDeclarationToMerge:ClassDeclaration)<-[:astNode]-(exportedClassScopeToMerge:Scope),
    (exportedClassDeclarationToMerge)-[:name]->(exportBindingIdentifierToDelete:BindingIdentifier),

// importer.js: import defaultName from "exporter";
    (importer:CompilationUnit)-[:contains]->(importerGlobalScope:GlobalScope)-[:children]->(importerModuleScope:Scope)
        -[:astNode]->(importerModule:Module)-[:items]->(import:ImportDeclaration)
        -[:defaultBinding]->(importBindingIdentifierToMerge:BindingIdentifier)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier

CREATE UNIQUE
    (exportedClassDeclarationToMerge)-[:name]->(importBindingIdentifierToMerge),
    (exportedClassDeclarationToMerge)<-[:items]-(importerModule),
    (exportedClassScopeToMerge)<-[:children]-(importerModuleScope)

DETACH DELETE
exportBindingIdentifierToDelete
;
