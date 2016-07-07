MATCH
  (fd:FunctionDeclaration)-[:body]->(b:FunctionBody)-[:statements]->(list:List)

MATCH (fd)    -[:`_end`]->  (fdE:End)
MATCH (list)  -[:`_end`]->  (listE:End)

MERGE (fd)    -[:`_normal`]-> (list)
MERGE (listE) -[:`_normal`]-> (fdE)
