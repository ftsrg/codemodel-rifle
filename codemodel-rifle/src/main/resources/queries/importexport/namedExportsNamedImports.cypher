MATCH
    (exporterModule:Module)-[:items]->(:ExportDeclaration)-[:namedExports]->(exportSpecifier:ExportSpecifier),
    (exporterModule)-[:items]->(:VariableDeclarationStatement)-[:declaration]->(:VariableDeclaration)-[:declarators]->(:VariableDeclarator)-[:binding]->(exportBindingIdentifier:BindingIdentifier),
    (importerModule:Module)-[:items]->(:ImportDeclaration)-[:namedImports]->(importSpecifier:ImportSpecifier)-[:binding]->(importBindingIdentifier:BindingIdentifier)<-[:node]-(variableImportDeclaration:Declaration)
WHERE
    exportSpecifier.exportedName = exportBindingIdentifier.name
    AND importBindingIdentifier.name = exportBindingIdentifier.name
CREATE UNIQUE
    (importSpecifier)-[:binding]->(exportBindingIdentifier),
    (variableImportDeclaration)-[:node]->(exportBindingIdentifier)
DETACH DELETE importBindingIdentifier
;
