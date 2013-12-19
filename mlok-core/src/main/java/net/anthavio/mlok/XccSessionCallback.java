package net.anthavio.mlok;

import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.XccException;

/**
 * 
 * @author martin.vanek
 *
 */
public interface XccSessionCallback<T> {

	public T doInSession(Session session) throws XccException;
}
