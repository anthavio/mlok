package net.anthavio.mlok.xdmp;

import java.util.Collection;

import net.anthavio.mlok.MarkLogicConnector;
import net.anthavio.mlok.query.Parameter;
import net.anthavio.mlok.query.Permission;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author vanek
 *
 */
public class DocumentInsert {

	private Logger log = LoggerFactory.getLogger(getClass());

	private static final char _ = '\'';

	private MarkLogicConnector connector;

	public DocumentInsert(MarkLogicConnector connector) {
		this.connector = connector;
	}

	public void insert(String uri, String document) {
		insert(uri, document, null, null, null, null);
	}

	public void insert(String uri, String document, Parameter<Permission>[] permissions, String[] collections,
			Integer quality, String[] forestIds) {
		/*
		ContentCreateOptions options = new ContentCreateOptions();
		Content content = ContentFactory.newContent(uri, document, options);
		Session session = connector.getContentSource().newSession();
		try {
			session.insertContent(content);
		} catch (RequestException rx) {
			rx.printStackTrace();
		}
		*/
		if (StringUtils.isBlank(uri)) {
			throw new IllegalArgumentException("uri is blank");
		}
		if (StringUtils.isBlank(document)) {
			throw new IllegalArgumentException("document is blank");
		}
		StringBuilder sb = new StringBuilder("xdmp:document-insert(");
		sb.append(_).append(uri).append(_);

		sb.append(',');
		sb.append(document);

		if (isAny(permissions)) {
			sb.append(',');
			sb.append('(');
			for (Parameter<Permission> permission : permissions) {
				sb.append("xdmp:permission(");
				sb.append(_).append(permission.getName()).append(_);
				sb.append(',').append(_).append(permission.getValue()).append(_);
				sb.append("),");
			}
			sb.deleteCharAt(sb.length());
			sb.append(')');
		} else {
			//sb.append("xdmp:default-permissions()");
		}

		if (isAny(collections)) {
			sb.append(',');
			sb.append('(');
			for (String collection : collections) {
				sb.append(_).append(collection).append(_);
				sb.append(',');
			}
			sb.deleteCharAt(sb.length());
			sb.append(')');
		} else {
			//sb.append("xdmp:default-collections()");
		}

		if (quality != null) {
			sb.append(',');
			sb.append(quality);
		} else {
			//sb.append(0);
		}

		if (isAny(forestIds)) {
			sb.append(',');
			sb.append('(');
			for (String forestId : forestIds) {
				sb.append(_).append(forestId).append(_);
				sb.append(',');
			}
			sb.deleteCharAt(sb.length());
			sb.append(')');
		}

		sb.append(')');
		connector.adhocQuery(sb.toString());
	}

	private final boolean isAny(Object[] items) {
		if (items != null && items.length > 0) {
			for (Object item : items) {
				if (item == null) {
					throw new IllegalArgumentException("Null item");
				}
			}
			return true;
		}
		return false;
	}

	private final boolean isAny(String[] items) {
		if (items != null && items.length > 0) {
			for (String item : items) {
				if (StringUtils.isBlank(item)) {
					throw new IllegalArgumentException("Blank item");
				}
			}
			return true;
		}
		return false;
	}

	private final boolean isAny(Collection<String> items) {
		if (items != null && items.size() > 0) {
			for (String item : items) {
				if (StringUtils.isBlank(item)) {
					throw new IllegalArgumentException("Blank item");
				}
			}
			return true;
		}
		return false;
	}
}
