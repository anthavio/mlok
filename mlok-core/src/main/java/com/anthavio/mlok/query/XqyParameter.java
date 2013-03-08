package com.anthavio.mlok.query;

import org.apache.commons.lang.StringUtils;

import com.marklogic.xcc.types.ValueType;

/**
 * 
 * @author martin.vanek
 *
 */
public class XqyParameter {

	protected final String name;

	protected final ValueType type;

	protected final XqyCardinality cardinality;

	public XqyParameter(String name) {
		this(name, null, null);
	}

	public XqyParameter(String name, ValueType type) {
		this(name, type, null);
	}

	public XqyParameter(String name, ValueType type, XqyCardinality cardinality) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("name is blank");
		}
		if (name.charAt(0) != '$') {
			name = "$" + name;
		}
		this.name = name;

		if (type == null && cardinality != null) {
			throw new IllegalArgumentException("Type must be known when Cardinality is specified");
		}
		this.type = type;//can be null
		this.cardinality = cardinality;//can be null
	}

	public String getXquery() {
		StringBuilder sb = new StringBuilder();
		this.addXquery(sb);
		return sb.toString();
	}

	public void addXquery(StringBuilder sb) {
		sb.append(name);
		if (type != null) {
			sb.append(" as ").append(type.toString());
			if (cardinality != null) {
				sb.append(cardinality.getValue());
			}
		}
	}

	public String toString() {
		return this.getXquery();
	}
}
