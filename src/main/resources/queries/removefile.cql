MATCH
	( cu:CompilationUnit
		{
			path: "path",
			sessionid: "sessionid"
		}
	)-[:contains]-(el)
DETACH DELETE
	cu, el
