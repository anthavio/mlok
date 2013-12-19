package net.anthavio.mlok;

import java.util.List;

import net.anthavio.mlok.result.XccResultItemMapper;
import net.anthavio.mlok.result.XccResultItemMapperExtractor;

import com.marklogic.xcc.types.XdmVariable;

/**
 * Use this when you need to return List of T from ResultSequence
 * 
 * @author martin.vanek
 *
 */
public abstract class RequestWithItemMapper<T> extends RequestWithExtractor<List<T>> {

	public RequestWithItemMapper(XccResultItemMapper<T> mapper, XdmVariable... variables) {
		super(new XccResultItemMapperExtractor<T>(mapper), variables);
	}
	/*
		public RequestWithItemMapperCallback(XccResultItemMapperExtractor<T> extractor, XdmVariable... variables) {
			super(extractor, variables);
		}
	*/
}
