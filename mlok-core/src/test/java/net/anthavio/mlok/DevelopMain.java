package net.anthavio.mlok;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anthavio.mlok.LibraryModule;
import net.anthavio.mlok.MarkLogicBrowser;
import net.anthavio.mlok.MarkLogicConnector;
import net.anthavio.mlok.query.XccAdhocFunction;

import org.apache.commons.io.FileUtils;


/**
 * 
 * /record[metadata/meta:doi='10.1038/nclimate1716']
 * 
 * @author martin.vanek
 *
 */
public class DevelopMain {

	public static void main(String[] args) {
		try {

			MarkLogicConnector connector = new MarkLogicConnector("xcc://test:pass@192.168.88.103:8003/Documents");
			connector.setStreamResult(false);
			/*
			Response response = connector.adhocFunctionOne(LibraryModule.SEARCH, "search", Response.class, "hello world");
			List<Result> results = response.getResult();
			for (Result result : results) {
				System.out.println(result);
			}
			if (true) {
				return;
			}
			*/

			String query = FileUtils.readFileToString(new File("src/test/resources/", "echo.xqy"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("input", "Silvio");
			//String query = "";
			List<String> list = connector.adhocQuery(query, String.class, parameters);
			for (String string : list) {
				System.out.println(string);
			}

			LibraryModule security = LibraryModule.SECURITY;

			XccAdhocFunction function = new XccAdhocFunction("fn:doc", "/mvanek/example.xml");
			function.addFunction("prdel", "'kozy'", null);
			List<String> adhocQuery = connector.adhocQuery(function, String.class);
			System.out.println(adhocQuery);

			List<String> adhocFunction = connector.adhocFunction("fn:doc", String.class, "/mvanek/example.xml");
			System.out.println(adhocFunction);

			MarkLogicBrowser browser = new MarkLogicBrowser(connector);
			List<String> uris = browser.listCollections();
			for (String uri : uris) {
				System.out.println("'" + uri + "'");
			}

			/*
			int size = resultSequence.size();
			System.out.println(size);
			while (resultSequence.hasNext()) {
				ResultItem item = resultSequence.next();
				System.out.println(item.asString());
			}

			resultSequence.close();
			*/
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
