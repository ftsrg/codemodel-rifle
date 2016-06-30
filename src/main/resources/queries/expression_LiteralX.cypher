MATCH (le)
WHERE
     "LiteralBooleanExpression" IN labels(le)
  OR "LiteralInfinityExpression" IN labels(le)
  OR "LiteralNullExpression" IN labels(le)
  OR "LiteralNumericExpression" IN labels(le)
  OR "LiteralRegExpExpression" IN labels(le)
  OR "LiteralStringExpression" IN labels(le)

MERGE
  (leS:StartProto)<-[:`_owns`]-(le)-[:`_owns`]->(leE:EndProto)
MERGE
  (leS)-[:`_normal`]->(leE)
