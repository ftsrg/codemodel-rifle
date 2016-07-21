MATCH
  (v:Variable)-[:references]->(r:Reference)-[:node]->
  (bide:BindingIdentifier)<-[:binding]-(exp:Expression),
  (exp)-[:`_type`]->(tag:Tag)

MERGE
  (v)-[:`_type`]->(tag)
