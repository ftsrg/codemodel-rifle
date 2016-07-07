MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration),

  (call)-[:arguments]->(params:List)

MATCH (call)    -[:`_end`]->  (callE:End)
MATCH (fd)      -[:`_end`]->  (fdE:End)
MATCH (params)  -[:`_end`]->  (pE:End)

MERGE (call)    -[:`_normal`]-> (params)
MERGE (paramsE) -[:`_normal`]-> (fd)
MERGE (fdE)     -[:`_normal`]-> (callE)
