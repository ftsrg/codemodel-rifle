MATCH
  (test:Node)<-[:test]-(if:IfStatement)-[:consequent]->(consequent:Node)
OPTIONAL MATCH
  (if)-[:alternate]->(alternate:Statement)

MERGE
  (ifS:StartProto)<-[:`_owns`]-(if)-[:`_owns`]->(ifE:EndProto)
MERGE
  (testS:StartProto)<-[:`_owns`]-(test)-[:`_owns`]->(testE:EndProto)
MERGE
  (consequentS:StartProto)<-[:`_owns`]-(consequent)-[:`_owns`]->(consequentE:EndProto)

MERGE
  (ifS)-[:`_normal`]->(testS)
MERGE
  (testE)-[:`_true`]->(consequentS)
MERGE
  (consequentE)-[:`_normal`]->(ifE)

FOREACH (alt IN CASE WHEN alternate IS NOT NULL THEN alternate ELSE [] END |
  MERGE
    (alternateS:StartProto)<-[:`_owns`]-(alt)-[:`_owns`]->(alternateE:EndProto)
  MERGE
    (testE)-[:`_false`]->(alternateS)
  MERGE
    (alternateE)-[:`_normal`]->(ifE)
)
