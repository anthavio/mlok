package net.anthavio.mlok;

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
public class ModuleInvokeWithItemCallback extends RequestWithItemCallback {

	private final String moduleUri;

	public ModuleInvokeWithItemCallback(String moduleUri, XccResultItemCallback callback, XdmVariable... variables) {
		super(callback);
		if (StringUtils.isBlank(moduleUri)) {
			throw new IllegalArgumentException("moduleUri is blank");
		}
		this.moduleUri = moduleUri;
	}

	@Override
	protected Request createRequest(Session session) {
		return session.newModuleInvoke(moduleUri);
	}

}
