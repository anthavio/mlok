package net.anthavio.mlok.query;

import com.marklogic.xcc.types.ValueType;

/**
 * 
 * @author vanek
 *
 */
public class XqyGlobalVariable extends XqyParameter {

	private final Object[] values;

	private boolean external;

	public XqyGlobalVariable(String name, boolean external) {
		this(name, null, null, external);
	}

	/**
	 * external global variable cannot be initialized by values
	 */
	public XqyGlobalVariable(String name, ValueType type, XqyCardinality cardinality, boolean external) {
		super(name, type, cardinality);
		this.external = external;
		this.values = null;
	}

	public XqyGlobalVariable(String name, Object... values) {
		this(name, null, null, values);
	}

	/**
	 * initialized global variable cannot be external
	 */
	public XqyGlobalVariable(String name, ValueType type, XqyCardinality cardinality, Object... values) {
		super(name, type, cardinality);
		this.external = false;
		this.values = values;

		if (type != null && external == false && (values == null || values.length == 0)) {
			throw new IllegalArgumentException("Typed nonexternal variable must be initialized");
		}
	}

	public String getXquery() {
		StringBuilder sb = new StringBuilder();
		this.addXquery(sb);
		return sb.toString();
	}

	public void addXquery(StringBuilder sb) {
		sb.append("declare variable ");
		super.addXquery(sb);

		if (external) {
			sb.append(" external");

		} else if (values != null && values.length > 0) {
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

	@Override
	public String toString() {
		return this.getXquery();
	}
}
