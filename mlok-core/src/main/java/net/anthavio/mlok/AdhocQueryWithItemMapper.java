package net.anthavio.mlok;

import net.anthavio.mlok.query.XccAdhocQuery;
import net.anthavio.mlok.result.XccResultItemMapper;

import org.apache.commons.lang.StringUtils;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author martin.vanek
 *
 */
public class AdhocQueryWithItemMapper<T> extends RequestWithItemMapper<T> {

	private final String xquery;

	public AdhocQueryWithItemMapper(XccAdhocQuery query, XccResultItemMapper<T> mapper) {
		super(mapper, query.getXdmVariables());
		xquery = query.getXquery();
	}

	public AdhocQueryWithItemMapper(String xquery, XccResultItemMapper<T> mapper, XdmVariable... variables) {
		super(mapper, variables);
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
