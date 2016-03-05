MATCH       // Match called FunctionDeclarations for every CallExpression
    (call:CallExpression)-[:callee]->(:IdentifierExpression)
    <--(:Either)<-[:node]-(:Reference)-[:references]-(:Variable)
    -[:declarations]->(:Declaration)-[:node]->(bindingIdentifier)
    <-[:name]-(fd:FunctionDeclaration)
MATCH          // List every call from a function body
    (fun:FunctionDeclaration)-[*]->(call:CallExpression)

MERGE       // Create a calls relationship between the caller
        // FunctionDeclaration and the called FunctionDeclaration
    (fun)-[:calls]->(fd)
