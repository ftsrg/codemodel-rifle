MATCH
  (l:List)
WHERE
  NOT (l)-[:`0`]->()

MERGE (l)     -[:`_end`]->  (lE:End)

MERGE (l)     -[:`_normal`]-> (lE)
