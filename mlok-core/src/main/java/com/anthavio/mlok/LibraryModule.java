package com.anthavio.mlok;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author martin.vanek
 *
 */
public class LibraryModule {

	public static LibraryModule ADMIN = new LibraryModule("admin", "http://marklogic.com/xdmp/admin",
			"/MarkLogic/admin.xqy");
	public static LibraryModule ALERT = new LibraryModule("alert", "http://marklogic.com/xdmp/alert",
			"/MarkLogic/alert.xqy");

	public static LibraryModule JSON = new LibraryModule("json", "http://marklogic.com/xdmp/json",
			"/MarkLogic/json/json.xqy");

	public static LibraryModule SEARCH = new LibraryModule("search", "http://marklogic.com/appservices/search",
			"/MarkLogic/appservices/search/search.xqy");

	public static LibraryModule SECURITY = new LibraryModule("sec", "http://marklogic.com/xdmp/security",
			"/MarkLogic/security.xqy");

	private final String prefix;
	private final URI namespaceUri;
	private final URI sourceUri;

	public LibraryModule(String prefix, String namespaceUri) {
		this(prefix, namespaceUri, null);
	}

	public LibraryModule(String prefix, String namespaceUri, String sourceUri) {
		if (StringUtils.isBlank(prefix)) {
			throw new IllegalArgumentException("prefix is blank");
		}
		this.prefix = prefix;

		if (StringUtils.isBlank(namespaceUri)) {
			throw new IllegalArgumentException("namespaceUri is blank");
		}
		this.namespaceUri = URI.create(namespaceUri);

		if (sourceUri != null) {
			this.sourceUri = URI.create(sourceUri);
		} else {
			this.sourceUri = null;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public URI getNamespaceUri() {
		return namespaceUri;
	}

	public URI getSourceUri() {
		return sourceUri;
	}

	public void addXquery(StringBuilder sb) {
		if (sourceUri == null) {
			sb.append("\tdeclare namespace ");
			sb.append(prefix);
			sb.append(" = '");
			sb.append(namespaceUri);
			sb.append("';\n");
		} else {
			sb.append("\timport module namespace ");
			sb.append(prefix);
			sb.append(" = '");
			sb.append(namespaceUri);
			sb.append("' at '");
			sb.append(sourceUri);
			sb.append("';\n");
		}
	}

	public String getXquery() {
		StringBuilder sb = new StringBuilder();
		addXquery(sb);
		return sb.toString();
	}

	@Override
	public String toString() {
		return "LibraryModule [prefix=" + prefix + ", namespaceUri=" + namespaceUri + ", sourceUri=" + sourceUri + "]";
	}

}
