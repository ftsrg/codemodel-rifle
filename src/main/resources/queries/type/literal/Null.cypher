MATCH
  (lit:LiteralNullExpression)

MERGE
  (lit)-[:`_type`]->(tag:Tag:`Null`)
