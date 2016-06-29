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
  (alternateS:StartProto)<-[:`_owns`]-(alternate)-[:`_owns`]->(alternateE:EndProto)

MERGE
  (testE)-[:`_true`]->(consequentS)
MERGE
  (testE)-[:`_false`]->(alternateS)

MERGE
  (consequentE)-[:`_normal`]->(ifE)
MERGE
  (alternateE)-[:`_normal`]->(ifE)
