package net.anthavio.mlok.result;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.exceptions.XccException;

/**
 * Equivalent of Spring RowCallbackHandler
 * 
 * @author martin.vanek
 *
 */
public interface XccResultItemCallback {

	void processItem(ResultItem item) throws XccException;

}
