MATCH
// exporter.js: export default class name1 { … };
    (exporter:CompilationUnit)-[:contains]->(exporterGlobalScope:GlobalScope)-[:children]->(exporterModuleScope:Scope)
        -[:astNode]->(exporterModule:Module)-[:items]->(:ExportDefault)
        -[:body]->(exportedClassDeclarationToMerge:ClassDeclaration)<-[:astNode]-(exportedClassScopeToMerge:Scope),

// importer.js: import { name1 } from "exporter";
    (importer:CompilationUnit)-[:contains]->(importerGlobalScope:GlobalScope)-[:children]->(importerModuleScope:Scope)
        -[:astNode]->(importerModule:Module)-[:items]->(import:ImportDeclaration)
        -[:defaultBinding]->(importBindingIdentifierToMerge:BindingIdentifier)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier

CREATE UNIQUE
    (exportedClassDeclarationToMerge)-[:name]->(importBindingIdentifierToMerge),
    (exportedClassDeclarationToMerge)<-[:items]-(importerModule),
    (exportedClassScopeToMerge)<-[:children]-(importerModuleScope)
;
