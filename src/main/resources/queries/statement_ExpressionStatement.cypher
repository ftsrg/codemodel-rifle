MATCH
  (es:ExpressionStatement)-[:expression]->(exp:Expression)

MATCH (es)  -[:`_end`]->  (esE:End)
MATCH (exp) -[:`_end`]->  (expE:End)

MERGE (es)    -[:`_normal`]-> (exp)
MERGE (expE)  -[:`_normal`]-> (esE)
