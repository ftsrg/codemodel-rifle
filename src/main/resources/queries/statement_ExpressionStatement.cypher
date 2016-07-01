MATCH
  (es:ExpressionStatement)-[:expression]->(exp:Expression)

MERGE
  (esS:StartProto)<-[:`_owns`]-(es)-[:`_owns`]->(esE:EndProto)
MERGE
  (expS:StartProto)<-[:`_owns`]-(exp)-[:`_owns`]->(expE:EndProto)

MERGE
  (esS)-[:`_normal`]->(expS)
MERGE
  (expE)-[:`_normal`]->(esE)
