MATCH
  (lit:LiteralStringExpression)

MERGE
  (lit)-[:`_type`]->(tag:Tag:String)
