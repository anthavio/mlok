package net.anthavio.mlok.query;

import net.anthavio.mlok.XqueryProvider;

import org.apache.commons.lang.StringUtils;

import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author martin.vanek
 *
 */
public class XccFunction implements XqueryProvider {

	private final String name;

	private final String body;

	private final XdmVariable[] parameters;

	private final ValueType returnType;

	public XccFunction(String name, String body) {
		this(name, body, null);
	}

	public XccFunction(String name, String body, ValueType returnType, XdmVariable... parameters) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("name is blank");
		}
		if (name.indexOf(":") == -1) {
			name = "local:" + name;
		}
		this.name = name;

		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("body is blank");
		}
		this.body = body;

		this.returnType = returnType; //can be null

		this.parameters = parameters; //can be null
	}

	public String getXquery() {
		StringBuilder sb = new StringBuilder();
		addXquery(sb);
		return sb.toString();
	}

	@Override
	public void addXquery(StringBuilder sb) {
		sb.append("declare function ");
		sb.append(name);
		sb.append('(');
		if (parameters != null && parameters.length != 0) {
			for (XdmVariable parameter : parameters) {
				sb.append(parameter.getName());
				if (parameter.getValue() != null) {
					sb.append(" as " + parameter.getValue().getValueType());
				}
				sb.append(',');
			}
			sb.deleteCharAt(sb.length() - 1);//remove trailing ','
		}
		sb.append(')');

		if (returnType != null) {
			sb.append(" as ").append(returnType.toString()).append('\n');
		}

		sb.append("{\n");
		sb.append(body);
		sb.append("\n};\n");
	}

}
