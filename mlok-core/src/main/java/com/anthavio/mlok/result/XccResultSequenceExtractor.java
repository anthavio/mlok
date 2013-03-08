package com.anthavio.mlok.result;

import org.springframework.dao.DataAccessException;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.XccException;

/**
 * Equivalent of Spring ResultSetExtractor
 * 
 * @author martin.vanek
 *
 */
public interface XccResultSequenceExtractor<T> {

	T extract(ResultSequence resultSequence) throws XccException, DataAccessException;
}
