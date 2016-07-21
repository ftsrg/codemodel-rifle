MATCH
  (lit:LiteralBooleanExpression)

MERGE
  (lit)-[:`_type`]->(tag:Tag:Boolean)
