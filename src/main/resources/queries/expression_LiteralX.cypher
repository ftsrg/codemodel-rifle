MATCH (le)
WHERE
     "LiteralBooleanExpression" IN labels(le)
  OR "LiteralInfinityExpression" IN labels(le)
  OR "LiteralNullExpression" IN labels(le)
  OR "LiteralNumericExpression" IN labels(le)
  OR "LiteralRegExpExpression" IN labels(le)
  OR "LiteralStringExpression" IN labels(le)

MATCH (le)    -[:`_end`]->  (leE:End)

MERGE (le)    -[:`_normal`]-> (leE)
