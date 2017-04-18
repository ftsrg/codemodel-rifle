MATCH
    (binaryExpression:BinaryExpression)-[:right]->(rightValue:LiteralNumericExpression)-[:location]->(:SourceSpan)
        -[:start]->(locationStart:SourceLocation)<-[:contains]-(containingCompilationUnit:CompilationUnit)

    WHERE
    binaryExpression.operator = 'Div'
    AND rightValue.value = '0.0'

RETURN
    'Division by zero' AS message,
    '' AS entityName,
    containingCompilationUnit.parsedFilePath AS compilationUnitPath,
    locationStart.line AS line,
    locationStart.column AS column
