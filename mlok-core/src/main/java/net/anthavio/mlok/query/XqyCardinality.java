package net.anthavio.mlok.query;

/**
 * 
 * @author vanek
 *
 */
public enum XqyCardinality {

	C1("", false, false), C01("?", false, false), C0N("*", true, false), C1N("+", true, true);

	private final String value;

	private final boolean multi;

	private final boolean required;

	private XqyCardinality(String value, boolean multi, boolean required) {
		this.value = value;
		this.multi = multi;
		this.required = required;
	}

	public String getValue() {
		return value;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isOptional() {
		return !required;
	}

	public boolean isMulti() {
		return multi;
	}

	public boolean isSingle() {
		return !multi;
	}
}
