package net.anthavio.mlok.result;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.exceptions.XccException;

/**
 * Equivalent of Spring RowMapper
 * 
 * @author martin.vanek
 *
 */
public interface XccResultItemMapper<T> {

	public T mapItem(ResultItem resultItem) throws XccException;
}
