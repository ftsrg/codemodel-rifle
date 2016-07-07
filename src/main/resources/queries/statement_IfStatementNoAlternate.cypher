MATCH
  (test:Node)<-[:test]-(if:IfStatement)-[:consequent]->(consequent:Node)
MATCH (if)          -[:`_end`]->  (ifE:End)
MATCH (test)        -[:`_end`]->  (testE:End)
MATCH (consequent)  -[:`_end`]->  (consequentE:End)

WHERE
  NOT (if)-[:alternate]->(:Statement)

MERGE (if)          -[:`_normal`]-> (test)
MERGE (testE)       -[:`_true`]->   (consequent)
MERGE (consequentE) -[:`_normal`]-> (ifE)
