package net.anthavio.mlok;

import net.anthavio.mlok.result.XccResultItemCallback;

import org.springframework.dao.DataAccessException;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.XccException;
import com.marklogic.xcc.types.XdmVariable;

/**
 * Suitable for stream processing
 * 
 * Use this when you need to do ResultSequence processing without returning anything
 * 
 * @author martin.vanek
 *
 */
public abstract class RequestWithItemCallback extends RequestWithCallback<Void> {

	private final XccResultItemCallback callback;

	public RequestWithItemCallback(XccResultItemCallback callback, XdmVariable... variables) {
		super(variables);
		if (callback == null) {
			throw new IllegalArgumentException("callback is null");
		}
		this.callback = callback;
	}

	protected Void process(ResultSequence resultSequence) throws DataAccessException, XccException {
		while (resultSequence.hasNext()) {
			callback.processItem(resultSequence.next());
		}
		return null;
	}

}
