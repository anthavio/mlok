package net.anthavio.mlok;

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
public class ModuleInvokeWithItemMapper<T> extends RequestWithItemMapper<T> {

	private final String moduleUri;

	public ModuleInvokeWithItemMapper(String moduleUri, XccResultItemMapper<T> mapper, XdmVariable... variables) {
		super(mapper);
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
