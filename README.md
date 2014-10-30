mlok
====
**This project is discontinued since I do not with MarkLogic anymore**

MarkLogic XCC is quite unfriendly and way too much DOM oriented.

```
      MarkLogicConnector connector = new MarkLogicConnector("xcc://username:password@localhost:8003/Documents");

			String query = FileUtils.readFileToString(new File("src/test/resources/", "echo.xqy"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("input", "Silvio");
			//String query = "";
			List<String> list = connector.adhocQuery(query, String.class, parameters);
			for (String string : list) {
				System.out.println(string);
			}

			XccAdhocFunction function = new XccAdhocFunction("fn:doc", "/mvanek/example.xml");
			function.addFunction("funame", "'string'", null);
			List<String> adhocQuery = connector.adhocQuery(function, String.class);
			System.out.println(adhocQuery);

			List<String> adhocFunction = connector.adhocFunction("fn:doc", String.class, "/mvanek/example.xml");
			System.out.println(adhocFunction);

			MarkLogicBrowser browser = new MarkLogicBrowser(connector);
			List<String> uris = browser.listCollections();
			for (String uri : uris) {
				System.out.println("'" + uri + "'");
			}
```
