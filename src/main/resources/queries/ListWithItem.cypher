MATCH
  (l:List)-[:`0`]->(first),
  (first)-[:`_next`*0..]->(last)
WHERE
  NOT (last)-[:`_next`]->()

MATCH (l)     -[:`_end`]->  (lE:End)
MATCH (last)  -[:`_end`]->  (lastE:End)

MERGE (l)     -[:`_normal`]-> (first)
MERGE (lastE) -[:`_normal`]-> (lE)
