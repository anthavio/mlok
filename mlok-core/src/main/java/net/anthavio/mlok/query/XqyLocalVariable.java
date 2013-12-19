package net.anthavio.mlok.query;

import com.marklogic.xcc.types.ValueType;

/**
 * 
 * @author martin.vanek
 *
 */
public class XqyLocalVariable extends XqyParameter {

	private final Object[] values;

	public XqyLocalVariable(String name) {
		this(name, null, null, (Object[]) null);
	}

	public XqyLocalVariable(String name, Object... values) {
		this(name, null, null, values);
	}

	public XqyLocalVariable(String name, ValueType type, Object... values) {
		this(name, type, null, values);
	}

	public XqyLocalVariable(String name, ValueType type, XqyCardinality cardinality, Object... values) {
		super(name, type, cardinality);
		if (values != null && values.length != 0) {
			this.values = values;
		} else {
			this.values = null;
		}
	}

	public String getXquery() {
		StringBuilder sb = new StringBuilder();
		addXquery(sb);
		return sb.toString();
	}

	public void addXquery(StringBuilder sb) {
		sb.append("let ");
		super.addXquery(sb);

		if (values != null && values.length > 0) {
			sb.append(" := ");
			if (values.length == 1) {
				Object value = values[0];
				if (type != null && type == ValueType.XS_STRING || value instanceof String) {
					sb.append('"').append(values[0]).append('"');
				} else {
					sb.append(values[0]);
				}
			} else { //multiple values
				if (cardinality != null && cardinality.isSingle()) {
					throw new IllegalArgumentException("Cardinality " + cardinality + " disallows multiple values");
				}
				sb.append('(');
				for (Object value : values) {
					if (type != null && type == ValueType.XS_STRING || value instanceof String) {
						sb.append('"').append(values[0]).append('"');
					} else {
						sb.append(values[0]);
					}
					sb.append(',');
				}
				sb.deleteCharAt(sb.length());
				sb.append(')');
			}
		}
		sb.append(";\n");
	}

	public String toString() {
		return getXquery();
	}

}
