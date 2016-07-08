MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration),

  (call)-[:arguments]->(params:List),

  (call)    -[:`_end`]->  (callE:End),
  (fd)      -[:`_end`]->  (fdE:End),
  (params)  -[:`_end`]->  (pE:End)

MERGE
  (call)    -[:`_normal`]-> (params)  -[:`_end`]->
  (pE)      -[:`_normal`]-> (fd)      -[:`_end`]->
  (fdE)     -[:`_normal`]-> (callE)
