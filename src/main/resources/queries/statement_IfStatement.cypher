MATCH
  (test:Node)<-[:test]-(if:IfStatement)-[:consequent]->(consequent:Node)
OPTIONAL MATCH
  (if)-[:alternate]->(alternate:Statement)

MERGE (if)          -[:`_end`]->  (ifE:End)
MERGE (test)        -[:`_end`]->  (testE:End)
MERGE (consequent)  -[:`_end`]->  (consequentE:End)

MERGE (if)          -[:`_normal`]-> (test)
MERGE (testE)       -[:`_true`]->   (consequent)
MERGE (consequentE) -[:`_normal`]-> (ifE)

FOREACH (alt IN CASE WHEN alternate IS NOT NULL THEN alternate ELSE [] END |
  MERGE (alt)       -[:`_end`]-> (alternateE:End)

  MERGE (testE)       -[:`_false`]->  (alternate)
  MERGE (alternateE)  -[:`_normal`]-> (ifE)
)
