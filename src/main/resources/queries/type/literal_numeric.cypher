MATCH
  (lit:LiteralNumericExpression)

MERGE
  (lit)-[:`_type`]->(tag:Tag:Numeric)
