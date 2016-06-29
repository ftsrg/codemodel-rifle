MATCH
  (bs:BlockStatement)-[:block]->(b:Block)-[:statements]->(:List)-[:`0`]->(s:Statement),
  (b)-[:statements]->(last:Statement)
WHERE
  NOT (last)-[:`_next`]->()

MERGE
  (bsS:StartProto)<-[:`_owns`]-(bs)-[:`_owns`]->(bsE:EndProto)
MERGE
  (sS:StartProto)<-[:`_owns`]-(s)-[:`_owns`]->(sE:EndProto)
MERGE
  (lastS:StartProto)<-[:`_owns`]-(last)-[:`_owns`]->(lastE:EndProto)

MERGE
  (bsS)-[:`_normal`]->(sS)
MERGE
  (lastE)-[:`_normal`]->(bsE)
