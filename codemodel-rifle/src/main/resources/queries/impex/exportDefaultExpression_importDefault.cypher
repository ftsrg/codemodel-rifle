MATCH
// exporter.js: export default 1 + 2;
    (exporter:CompilationUnit)-[:contains]->(:ExportDefault)-[:body]->(expressionToMerge:Expression),

// importer.js: import defaultName from "exporter";
    (importer:CompilationUnit)-[:contains]->(import:ImportDeclaration)
        -[:defaultBinding]->(:BindingIdentifier)

    WHERE
    exporter.parsedFilePath CONTAINS import.moduleSpecifier

CREATE UNIQUE
    (import)-[:init]->(expressionToMerge)
;
