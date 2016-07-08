MATCH
  (l:List)-[:`0`]->(first),
  (first)-[:`_next`*0..]->(last),

  (l)     -[:`_end`]->  (lE:End),
  (last)  -[:`_end`]->  (lastE:End)

WHERE
  NOT (last)-[:`_next`]->()

MERGE (l)     -[:`_normal`]-> (first)
MERGE (lastE) -[:`_normal`]-> (lE)
