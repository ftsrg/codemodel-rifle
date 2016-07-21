MATCH
  (lit:LiteralRegExpExpression)

MERGE
  (lit)-[:`_type`]->(tag:Tag:RegExp)
