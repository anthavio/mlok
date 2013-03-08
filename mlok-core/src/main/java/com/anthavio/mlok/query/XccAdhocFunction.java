package com.anthavio.mlok.query;

import org.apache.commons.lang.StringUtils;

import com.anthavio.mlok.LibraryModule;
import com.anthavio.mlok.XdmVariables;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author vanek
 *
 */
public class XccAdhocFunction extends XccAdhocQuery {

	public XccAdhocFunction(String functionName, Object... arguments) {
		this(null, functionName, XdmVariables.buildXdmVariables(arguments));
	}

	public XccAdhocFunction(String functionName, XdmVariable... variables) {
		this(null, functionName, variables);
	}

	public XccAdhocFunction(LibraryModule module, String functionName, Object... arguments) {
		this(module, functionName, XdmVariables.buildXdmVariables(arguments));
	}

	public XccAdhocFunction(LibraryModule module, String functionName, XdmVariable... variables) {
		//module CAN be null for built in functions (xdmp:... , fn:..., ...)
		if (module != null) {
			addModule(module);
		}

		if (StringUtils.isBlank(functionName)) {
			throw new IllegalArgumentException("functionName is blank");
		} else if (functionName.indexOf(' ') != -1) {
			throw new IllegalArgumentException("functionName '" + functionName + "' is invalid");
		}

		int cidx = functionName.indexOf(":");
		if (module == null && cidx == -1) {
			throw new IllegalArgumentException("Builtin function name MUST be prefixed");
		}
		if (module != null && cidx != -1) {
			throw new IllegalArgumentException("Imported function name must NOT be prefixed");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		if (module != null) {
			sb.append(module.getPrefix());
			sb.append(":");
		}
		sb.append(functionName);
		sb.append("(");
		if (variables != null && variables.length != 0) {
			for (int i = 0; i < variables.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("$");
				XdmVariable variable = variables[i];
				sb.append(variable.getName());
				addVariable(variable);
			}
		}
		sb.append(")");

		super.setBody(sb.toString());
	}

	@Override
	public void setBody(String body) {
		throw new IllegalStateException("Body is generated for adhoc function call");
	}

}
