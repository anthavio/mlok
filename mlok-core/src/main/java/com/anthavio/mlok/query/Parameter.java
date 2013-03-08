package com.anthavio.mlok.query;

public class Parameter<T> {

	private final String name;

	private final T value;

	public Parameter(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Parameter ['" + name + "'='" + value + "']";
	}

}
