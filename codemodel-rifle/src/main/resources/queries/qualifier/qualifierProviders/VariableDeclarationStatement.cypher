MATCH
    (variableDeclarationStatement:VariableDeclarationStatement)
        -[:declaration]->(variableDeclaration:VariableDeclaration)
        -[:declarators]->(variableDeclarator:VariableDeclarator)
        -[:binding]->(:BindingIdentifier)-[:_qualifier]->(qualifier:Qualifier)

MERGE
    (variableDeclarationStatement)-[:_qualifier]->(qualifier)

MERGE
    (variableDeclaration)-[:_qualifier]->(qualifier)

MERGE
    (variableDeclaration)-[:_qualifier]->(qualifier)
