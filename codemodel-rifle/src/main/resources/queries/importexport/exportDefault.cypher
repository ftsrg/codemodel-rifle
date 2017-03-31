MATCH
    (exporter:CompilationUnit)-[:contains]->(exportDeclaration:ExportDeclaration)-[:body]->(expression),
    (importDeclaration:ImportDeclaration)-[:defaultBinding]->(importIdentifier:BindingIdentifier)<-[:node]-(declarationToImport:Declaration)
WHERE
    (NOT (declarationToImport)-[:declaration]->())
    AND exporter.parsedFilePath CONTAINS importDeclaration.moduleSpecifier
CREATE
    (declarationToImport)-[:declaration]->(expression)
;
