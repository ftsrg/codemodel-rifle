MATCH
  (call:CallExpression)-[:callee]->(:IdentifierExpression)
  <-[:node]-(:Reference)<-[:references]-(:Variable)
  -[:declarations]->(:Declaration)-[:node]->(:BindingIdentifier)
  <-[:name]-(fd:FunctionDeclaration),

  (call)-[:arguments]->(params:List)-[:`0`]->(firstP),

  (call)-[:arguments]->(lastP)
WHERE
  NOT (lastP)-[:`_next`]->()

MERGE
  (callS:StartProto)<-[:`_owns`]-(call)-[:`_owns`]->(callE:EndProto)
MERGE
  (pS:StartProto)<-[:`_owns`]-(params)-[:`_owns`]->(pE:EndProto)
MERGE
  (fdS:StartProto)<-[:`_owns`]-(fd)-[:`_owns`]->(fdE:EndProto)
MERGE
  (firstS:StartProto)<-[:`_owns`]-(firstP)-[:`_owns`]->(firstE:EndProto)
MERGE
  (lastS:StartProto)<-[:`_owns`]-(lastP)-[:`_owns`]->(lastE:EndProto)

MERGE
  (callS)-[:`_normal`]->(pS)-[:`_normal`]->(firstS)
MERGE
  (lastE)-[:`_normal`]->(fdS)
MERGE
  (fdE)-[:`_normal`]->(callE)
