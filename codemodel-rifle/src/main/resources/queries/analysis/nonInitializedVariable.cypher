MATCH
    (:CallExpression)-[:arguments]->(:VariableReference)<-[:node]-(:Reference)<-[:references]-(subjectVariable:Variable)
        -[:declarations]->(:Declaration)-[:node]->(:VariableReference)
        <-[:binding]-(variableDeclarator:VariableDeclarator),
    (subjectVariable)<-[:contains]-(containingCompilationUnit:CompilationUnit)

    WHERE NOT (variableDeclarator)-[:init]->()

RETURN
    "Non-initialized variable:" AS message,
    subjectVariable.name AS variableName,
    containingCompilationUnit.parsedFilePath AS compilationUnitPath
