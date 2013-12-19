package net.anthavio.mlok;

import net.anthavio.mlok.query.XccAdhocQuery;
import net.anthavio.mlok.result.XccResultItemCallback;

import org.apache.commons.lang.StringUtils;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.types.XdmVariable;

/**
 * Suitable for stream processing
 * 
 * @author martin.vanek
 *
 */
public class AdhocQueryWithItemCallback extends RequestWithItemCallback {

	private final String xquery;

	public AdhocQueryWithItemCallback(XccAdhocQuery query, XccResultItemCallback callback) {
		super(callback, query.getXdmVariables());
		xquery = query.getXquery();
	}

	public AdhocQueryWithItemCallback(String xquery, XccResultItemCallback callback, XdmVariable... variables) {
		super(callback, variables);
		if (StringUtils.isBlank(xquery)) {
			throw new IllegalArgumentException("xquery is blank");
		}
		this.xquery = xquery;
	}

	@Override
	protected Request createRequest(Session session) {
		log.debug("AdhocQuery\n" + xquery);
		return session.newAdhocQuery(xquery);
	}

}
