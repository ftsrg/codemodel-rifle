MATCH
  (vds:VariableDeclarationStatement)-[:declaration]->(vdion:VariableDeclaration)
  -[:declarators]->(vdor:VariableDeclarator)-[:init]->(exp:Expression)

MERGE (vds)     -[:`_end`]->  (vdsE:End)
MERGE (vdion)   -[:`_end`]->  (vdionE:End)
MERGE (exp)     -[:`_end`]->  (expE:End)

MERGE (vdion)   -[:`_normal`]-> (vdionE)

MERGE (vds)     -[:`_normal`]-> (exp)
MERGE (expE)    -[:`_normal`]-> (vdion)
MERGE (vdionE)  -[:`_normal`]-> (vdsE)
