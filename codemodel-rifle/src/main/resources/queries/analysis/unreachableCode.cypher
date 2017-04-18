MATCH
    (containingCompilationUnit:CompilationUnit)-[:contains]->(ifStatement:IfStatement)
        -[:test]->(literalBooleanExpression:LiteralBooleanExpression),
    (ifStatement)-[:consequent]->(:BlockStatement)-[:location]->(:SourceSpan)-[:start]->(entityLocation:SourceLocation)

    WHERE
    literalBooleanExpression.value = 'false'

RETURN
    'Unreachable code' AS message,
    '' AS entityName,
    containingCompilationUnit.parsedFilePath AS compilationUnitPath,
    entityLocation.line AS line,
    entityLocation.column AS column
;
