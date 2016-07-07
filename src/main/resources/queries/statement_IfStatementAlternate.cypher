MATCH
  (test:Node)<-[:test]-(if:IfStatement)-[:consequent]->(consequent:Node),
  (if)-[:alternate]->(alternate:Statement)

MATCH (alternate)   -[:`_end`]->    (alternateE:End)

MATCH (if)          -[:`_end`]->  (ifE:End)
MATCH (test)        -[:`_end`]->  (testE:End)
MATCH (consequent)  -[:`_end`]->  (consequentE:End)

MERGE (if)          -[:`_normal`]-> (test)
MERGE (testE)       -[:`_true`]->   (consequent)
MERGE (consequentE) -[:`_normal`]-> (ifE)

MERGE (testE)       -[:`_false`]->  (alternate)
MERGE (alternateE)  -[:`_normal`]-> (ifE)
