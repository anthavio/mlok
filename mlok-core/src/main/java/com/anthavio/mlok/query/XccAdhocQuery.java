package com.anthavio.mlok.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthavio.mlok.LibraryModule;
import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XName;
import com.marklogic.xcc.types.XdmVariable;

/**
 * http://www.xquery.co.uk/tutorial_xquery_get_started/get_tutorial_page/xquery_main_module
 * 
 * @author vanek
 *
 */
public class XccAdhocQuery {

	public static enum XqueryVersion {
		V10ML("xquery version '1.0-ml';");

		private String value;

		private XqueryVersion(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private String body;

	private XqueryVersion version = XqueryVersion.V10ML;

	private Map<URI, LibraryModule> modules = new HashMap<URI, LibraryModule>();

	private Map<XName, XdmVariable> variables = new HashMap<XName, XdmVariable>();

	private List<XccFunction> functions;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	};

	public XccAdhocQuery addModule(String prefix, String namespace, String uri) {
		return addModule(new LibraryModule(prefix, namespace, uri));
	}

	public XccAdhocQuery addModule(LibraryModule module) {
		if (module == null) {
			throw new IllegalArgumentException("module is null");
		}
		if (modules.get(module.getNamespaceUri()) != null) {
			throw new IllegalArgumentException("Library module already declared " + variables.get(module.getNamespaceUri()));
		}
		modules.put(module.getNamespaceUri(), module);
		return this;
	}

	/*
		public XccAdhocQuery addVariable(XqyGlobalVariable variable) {
			XdmVariable xdmVariable=convert...
			return addVariable(xdmVariable);
		}
	*/

	public XccAdhocQuery addVariable(XdmVariable variable) {
		if (variable == null) {
			throw new IllegalArgumentException("variable is null");
		}
		if (variables.get(variable.getName()) != null) {
			throw new IllegalArgumentException("Global variable already declared " + variables.get(variable.getName()));
		}
		this.variables.put(variable.getName(), variable);
		return this;
	}

	public XccAdhocQuery addFunction(XccFunction function) {
		if (functions == null) {
			functions = new ArrayList<XccFunction>();
		}
		functions.add(function);
		return this;
	}

	public XccAdhocQuery addFunction(String functionName, String functionBody, ValueType returnType,
			XdmVariable... parameters) {
		XccFunction function = new XccFunction(functionName, functionBody, returnType, parameters);
		return addFunction(function);
	}

	public XdmVariable[] getXdmVariables() {
		Collection<XdmVariable> values = variables.values();
		return values.toArray(new XdmVariable[values.size()]);
	}

	public String getXquery() {

		StringBuilder sb = new StringBuilder();

		sb.append(version.getValue()).append('\n');

		if (modules != null && modules.size() != 0) {
			for (LibraryModule module : modules.values()) {
				module.addXquery(sb);
			}
		}

		if (variables != null && variables.size() != 0) {
			for (XdmVariable variable : variables.values()) {
				sb.append("\tdeclare variable $");
				sb.append(variable.getName().toString());
				if (variable.getValue().getValueType() != null) {
					sb.append(" as ");
					sb.append(variable.getValue().getValueType().toString());
					//XXX XqyCardinality...
				}
				sb.append(" external;");
				//if (variable.getValue() instanceof XdmAtomic) {
				sb.append(" (: ").append(variable.getValue().asString()).append(" :)");
				//}
				sb.append('\n');
			}
		}

		if (functions != null) {
			for (XccFunction function : functions) {
				function.addXquery(sb);
			}
		}

		sb.append(body);

		return sb.toString();
	}

	public String toString() {
		return getXquery();
	}

}
