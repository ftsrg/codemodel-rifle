MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration)
WHERE
  NOT (call)-[:arguments]->()

MERGE
  (callS:StartProto)<-[:`_owns`]-(call)-[:`_owns`]->(callE:EndProto)
MERGE
  (fdS:StartProto)<-[:`_owns`]-(fd)-[:`_owns`]->(fdE:EndProto)

MERGE
  (callS)-[:`_normal`]->(fdS)
MERGE
  (fdE)-[:`_normal`]->(callE)
