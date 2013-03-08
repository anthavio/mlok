package com.anthavio.mlok;

/**
 * Eqivalent to SqlProvider
 * 
 * Interface to be implemented by objects that can provide Xquery strings.
 * 
 * @author martin.vanek
 *
 */
public interface XqueryProvider {

	public String getXquery();

	public void addXquery(StringBuilder sb);
}
