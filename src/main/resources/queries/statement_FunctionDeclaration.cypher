MATCH
  (fd:FunctionDeclaration)-[:body]->(b:FunctionBody)-[:statements]->(list:List)

MERGE (fd)    -[:`_end`]->  (fdE:End)
MERGE (list)  -[:`_end`]->  (listE:End)

MERGE (fd)    -[:`_normal`]-> (list)
MERGE (listE) -[:`_normal`]-> (fdE)
