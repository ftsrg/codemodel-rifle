MATCH
  (fd:FunctionDeclaration)-[:body]->(b:FunctionBody)-[:statements]->(:List)-[:`0`]->(s:Statement),
  (b)-[:statements]->(last:Statement)
WHERE
  NOT (last)-[:`_next`]->()

MERGE
  (fdS:StartProto)<-[:`_owns`]-(fd)-[:`_owns`]->(fdE:EndProto)
MERGE
  (sS:StartProto)<-[:`_owns`]-(s)-[:`_owns`]->(sE:EndProto)
MERGE
  (lastS:StartProto)<-[:`_owns`]-(last)-[:`_owns`]->(lastE:EndProto)

MERGE
  (fdS)-[:`_normal`]->(sS)
MERGE
  (lastE)-[:`_normal`]->(fdE)
