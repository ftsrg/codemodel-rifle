MATCH
  (bs:BlockStatement)-[:block]->(b:Block)-[:statements]->(list:List)

MERGE (bs)    -[:`_end`]->  (bsE:End)
MERGE (list)  -[:`_end`]->  (listE:End)

MERGE (bs)    -[:`_normal`]-> (list)
MERGE (listE) -[:`_normal`]-> (bsE)
