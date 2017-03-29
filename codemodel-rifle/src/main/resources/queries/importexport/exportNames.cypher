MATCH
    (exporter:CompilationUnit)-[:contains]->(exportDeclaration:ExportDeclaration)-[:namedExports]->(exportSpecifier:ExportSpecifier),
    (exporterModule:Module)-[:items]->(exportDeclaration),
    (exporterModule)-[:items]->(:VariableDeclarationStatement)-[:declaration]->(:VariableDeclaration)-[:declarators]->(:VariableDeclarator)-[:binding]->(bindingIdentifier:BindingIdentifier),
    (importDeclaration:ImportDeclaration)-[:namespaceBinding]->(importIdentifier:BindingIdentifier),
    (variable:Variable)
WHERE
    exportSpecifier.exportedName CONTAINS bindingIdentifier.name
    AND variable.name = bindingIdentifier.name
CREATE
    (importIdentifier)-[:imported]->(variable)
;
