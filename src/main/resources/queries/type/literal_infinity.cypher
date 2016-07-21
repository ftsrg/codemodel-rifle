MATCH
  (lit:LiteralInfinityExpression)

MERGE
  (lit)-[:`_type`]->(tag:Tag:Infinity)
