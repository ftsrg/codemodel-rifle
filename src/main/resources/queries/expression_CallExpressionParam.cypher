MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration),

  (call)-[:arguments]->(params:List)

MERGE (call)    -[:`_end`]->  (callE:End)
MERGE (fd)      -[:`_end`]->  (fdE:End)
MERGE (params)  -[:`_end`]->  (pE:End)

MERGE (call)    -[:`_normal`]-> (params)
MERGE (paramsE) -[:`_normal`]-> (fd)
MERGE (fdE)     -[:`_normal`]-> (callE)
