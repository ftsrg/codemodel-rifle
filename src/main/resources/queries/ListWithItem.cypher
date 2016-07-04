MATCH
  (l:List)-[:`0`]->(first),
  (first)-[:`_next`*]->(last)
WHERE
  NOT (last)-[:`_next`]->()

MERGE (l)     -[:`_end`]->  (lE:End)
MERGE (last)  -[:`_end`]->  (lastE:End)

MERGE (l)     -[:`_normal`]-> (first)
MERGE (lastE) -[:`_normal`]-> (lE)
