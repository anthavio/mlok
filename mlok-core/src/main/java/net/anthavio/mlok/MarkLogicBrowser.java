package net.anthavio.mlok;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.anthavio.mlok.query.XccAdhocQuery;
import net.anthavio.mlok.result.XccResultItemMapper;

import org.springframework.dao.DataAccessException;

import com.marklogic.xcc.ValueFactory;
import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XName;
import com.marklogic.xcc.types.XdmValue;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author martin.vanek
 *
 */
public class MarkLogicBrowser {

	private MarkLogicConnector connector;

	public MarkLogicBrowser(MarkLogicConnector connector) {
		this.connector = connector;
	}

	public List<URI> listContents() throws DataAccessException {
		return list(true, null, null);
	}

	public List<URI> listContents(String includePattern, String excludePattern) throws DataAccessException {
		return list(true, includePattern, excludePattern);
	}

	public List<URI> listDocuments() throws DataAccessException {
		return list(false, null, null);
	}

	public List<URI> listDocuments(String includePattern, String excludePattern) throws DataAccessException {
		return list(false, includePattern, excludePattern);
	}

	public List<String> listCollections() {
		StringBuilder sb = new StringBuilder();
		//sb.append("xquery version '1.0-ml' encoding 'UTF-8';\n");
		sb.append("for $collection in cts:collections()\n");
		//sb.append("return (xs:string($collection), '&#xa;')");
		sb.append("return xdmp:to-json($collection)");

		XccAdhocQuery query = new XccAdhocQuery();
		query.setBody(sb.toString());
		return connector.adhocQuery(query, String.class);
	}

	public String getDocument(String uri) throws DataAccessException {
		XName name = new XName(null, "uri");
		XdmValue value = ValueFactory.newValue(ValueType.XS_ANY_URI, uri);
		XdmVariable xdmVariable = ValueFactory.newVariable(name, value);
		List<String> list = connector.adhocFunction(null, "fn:doc", String.class, xdmVariable);
		return list.get(0);
	}

	public <T> T getDocument(String uri, XccResultItemMapper<T> mapper) throws DataAccessException {
		XName name = new XName(null, "uri");
		XdmValue value = ValueFactory.newValue(ValueType.XS_ANY_URI, uri);
		XdmVariable xdmVariable = ValueFactory.newVariable(name, value);
		List<T> list = connector.adhocFunction(null, "fn:doc", mapper, xdmVariable);
		return list.get(0);
	}

	//-- Private Implementation ----------------------------------------------//
	private List<URI> list(boolean includeFolders, String includePattern, String excludePattern) {
		List<XdmVariable> vars = new ArrayList<XdmVariable>();
		List<String> clauses = new ArrayList<String>();
		if (!includeFolders) {
			clauses.add("not(ends-with($uri, '/'))");
		}
		if (includePattern != null) {
			vars.add(XdmVariables.buildXdmVariable("includePattern", ValueType.XS_STRING, includePattern));
			clauses.add("matches($uri, $includePattern)");
		}
		if (excludePattern != null) {
			vars.add(XdmVariables.buildXdmVariable("excludePattern", ValueType.XS_STRING, excludePattern));
			clauses.add("not(matches($uri, $excludePattern))");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("xquery version '1.0-ml' encoding 'UTF-8';\n");
		for (int i = 0; i < vars.size(); i++) {
			sb.append("declare variable $");
			sb.append(vars.get(i).getName());
			sb.append(" external;\n");
		}
		sb.append("for $node in xdmp:document-properties()\n");
		sb.append("let $uri := xdmp:node-uri($node)\n");
		if (clauses.size() > 0) {
			sb.append("where ");
			for (int i = 0; i < clauses.size(); i++) {
				if (i > 0) {
					sb.append("and ");
				}
				sb.append(clauses.get(i)).append("\n");
			}
		}
		sb.append("\torder by $uri\n");
		sb.append("\treturn xs:anyURI($uri)\n");
		return connector.adhocQuery(sb.toString(), URI.class, vars.toArray(new XdmVariable[vars.size()]));
	}
}
