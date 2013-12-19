package net.anthavio.mlok;

import net.anthavio.mlok.result.XccResultSequenceExtractor;

import org.springframework.dao.DataAccessException;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.XccException;
import com.marklogic.xcc.types.XdmVariable;

/**
 * Use this when you need to return single T from ResultSequence
 * 
 * @author martin.vanek
 *
 */
public abstract class RequestWithExtractor<T> extends RequestWithCallback<T> {

	private final XccResultSequenceExtractor<T> extractor;

	public RequestWithExtractor(XccResultSequenceExtractor<T> extractor, XdmVariable... variables) {
		super(variables);
		if (extractor == null) {
			throw new IllegalArgumentException("extractor is null");
		}
		this.extractor = extractor;
	}

	protected T process(ResultSequence resultSequence) throws DataAccessException, XccException {
		return extractor.extract(resultSequence);
	}

	protected abstract Request createRequest(Session session);
}
