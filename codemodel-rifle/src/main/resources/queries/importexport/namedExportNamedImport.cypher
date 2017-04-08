MATCH
// export.js: export { name1 };
    (simpleNamedExport_exporter:CompilationUnit)-[:contains]->(:ExportDeclaration)
        -[:namedExports]->(simpleNamedExport_exportSpecifier:ExportSpecifier),
    (simpleNamedExport_exporter)-[:contains]->(simpleNamedExport_exportedVariable:Variable)
        -[:declarations]->(simpleNamedExport_declarationToMerge:Declaration)
        -[:node]->(simpleNamedExport_exportBindingIdentifierToMerge:BindingIdentifier),
    (simpleNamedExport_exportedVariable)-[:declarations]->(simpleNamedExport_declarationListToMerge:List),

// import.js: import { name1 } from "export";
    (simpleNamedImport_importer:CompilationUnit)-[:contains]->(simpleNamedImport_importDeclaration:ImportDeclaration)
        -[:namedImports]->(simpleNamedImport_importSpecifier:ImportSpecifier)
        -[:binding]->(simpleNamedImport_importBindingIdentifierToDelete:BindingIdentifier),
    (simpleNamedImport_importer)-[:contains]->(simpleNamedImport_importedVariable:Variable),
    (simpleNamedImport_importedVariable)-[:declarations]->(simpleNamedImport_declarationToDelete:Declaration),
    (simpleNamedImport_importedVariable)-[:declarations]->(simpleNamedImport_declarationListToDelete:List)

    WHERE
    simpleNamedExport_exporter.parsedFilePath CONTAINS simpleNamedImport_importDeclaration.moduleSpecifier
    AND simpleNamedExport_exportSpecifier.exportedName = simpleNamedExport_exportedVariable.name
    AND simpleNamedExport_exportSpecifier.exportedName = simpleNamedImport_importBindingIdentifierToDelete.name
    AND simpleNamedExport_exportSpecifier.exportedName = simpleNamedImport_importedVariable.name
    AND simpleNamedExport_exportSpecifier.exportedName = simpleNamedExport_exportBindingIdentifierToMerge.name

CREATE UNIQUE
    (simpleNamedImport_importedVariable)-[:declarations]->(simpleNamedExport_declarationToMerge),
    (simpleNamedImport_importedVariable)-[:declarations]->(simpleNamedExport_declarationListToMerge),
    (simpleNamedImport_importSpecifier)-[:binding]->(simpleNamedExport_exportBindingIdentifierToMerge)

DETACH DELETE
simpleNamedImport_declarationToDelete,
simpleNamedImport_declarationListToDelete,
simpleNamedImport_importBindingIdentifierToDelete
;
