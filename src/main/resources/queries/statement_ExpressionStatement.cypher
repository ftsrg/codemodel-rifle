MATCH
  (es:ExpressionStatement)-[:expression]->(exp:Expression)

MERGE (es)  -[:`_end`]->  (esE:End)
MERGE (exp) -[:`_end`]->  (expE:End)

MERGE (es)    -[:`_normal`]-> (exp)
MERGE (expE)  -[:`_normal`]-> (esE)
