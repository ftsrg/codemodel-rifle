MATCH
    (qualifier:Qualifier)<-[:_qualifier]-(functionDeclaration:FunctionDeclaration)
        -[:name]->(bindingIdentifier:BindingIdentifier)

MERGE
    (bindingIdentifier)-[:_qualifier]->(qualifier)
