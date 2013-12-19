package net.anthavio.mlok;

import org.apache.commons.lang.StringUtils;
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
public class ModuleSpawnWithoutCallback extends RequestWithCallback<Void> {

	private String moduleUri;

	public ModuleSpawnWithoutCallback(String moduleUri, XdmVariable... variables) {
		super(variables);
		if (StringUtils.isBlank(moduleUri)) {
			throw new IllegalArgumentException("moduleUri is blank");
		}
		this.moduleUri = moduleUri;
	}

	@Override
	protected Request createRequest(Session session) {
		return session.newModuleSpawn(moduleUri);
	}

	@Override
	protected Void process(ResultSequence resultSequence) throws DataAccessException, XccException {
		throw new IllegalStateException("What what what!?!?");//XXX examine what is returned from spawn
	}

}
