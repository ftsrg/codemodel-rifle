MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration)
WHERE
  NOT (call)-[:arguments]->()

MATCH (call)    -[:`_end`]->  (callE:End)
MATCH (fd)      -[:`_end`]->  (fdE:End)

MERGE (call)    -[:`_normal`]-> (fd)
MERGE (fdE)     -[:`_normal`]-> (callE)
