MATCH
  (bs:BlockStatement)-[:block]->(b:Block)-[:statements]->(list:List)

MATCH (bs)    -[:`_end`]->  (bsE:End)
MATCH (list)  -[:`_end`]->  (listE:End)

MERGE (bs)    -[:`_normal`]-> (list)
MERGE (listE) -[:`_normal`]-> (bsE)
