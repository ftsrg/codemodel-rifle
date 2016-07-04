MATCH
  (l:List)-[:`0`]->(first),
  (first)-[:`_next`*0..]->(last)
WHERE
  NOT (last)-[:`_next`]->()

MERGE (l)     -[:`_end`]->  (lE:End)
MERGE (last)  -[:`_end`]->  (lastE:End)

MERGE (l)     -[:`_normal`]-> (first)
MERGE (lastE) -[:`_normal`]-> (lE)
