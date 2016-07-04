MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration)
WHERE
  NOT (call)-[:arguments]->()

MERGE (call)    -[:`_end`]->  (callE:End)
MERGE (fd)      -[:`_end`]->  (fdE:End)

MERGE (call)    -[:`_normal`]-> (fd)
MERGE (fdE)     -[:`_normal`]-> (callE)
