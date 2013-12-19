package net.anthavio.mlok;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.XccException;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author martin.vanek
 *
 */
public abstract class RequestWithCallback<T> implements XccSessionCallback<T> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final XdmVariable[] variables;

	public RequestWithCallback(XdmVariable... variables) {
		//maybe iterate and remove nulls
		this.variables = variables;
	}

	public List<XdmVariable> getVariables() {
		return variables == null ? Collections.<XdmVariable> emptyList() : Arrays.asList(variables);
	}

	@Override
	public T doInSession(Session session) throws XccException {
		Request request = createRequest(session);
		if (variables != null && variables.length != 0) {
			for (XdmVariable variable : variables) {
				if (log.isDebugEnabled()) {
					log.debug("Setting Request XdmVariable name: " + variable.getName() + " value: " + variable.getValue());
				}
				request.setVariable(variable);
			}
		}
		return process(session.submitRequest(request));
	}

	protected abstract Request createRequest(Session session);

	protected abstract T process(ResultSequence resultSequence) throws DataAccessException, XccException;

}
