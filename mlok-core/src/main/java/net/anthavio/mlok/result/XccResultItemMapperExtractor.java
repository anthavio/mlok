package net.anthavio.mlok.result;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.XccException;

/**
 * Equivalent of Spring RowMapperResultSetExtractor
 * 
 * @author martin.vanek
 *
 */
public class XccResultItemMapperExtractor<T> implements XccResultSequenceExtractor<List<T>> {

	private final XccResultItemMapper<T> mapper;

	private final int itemsExpected;

	public XccResultItemMapperExtractor(XccResultItemMapper<T> mapper) {
		this(mapper, 0);
	}

	public XccResultItemMapperExtractor(XccResultItemMapper<T> mapper, int itemsExpected) {
		if (mapper == null) {
			throw new IllegalArgumentException("mapper is null");
		}
		this.mapper = mapper;
		this.itemsExpected = itemsExpected;
	}

	@Override
	public List<T> extract(ResultSequence rs) throws DataAccessException, XccException {
		List<T> results = (this.itemsExpected > 0 ? new ArrayList<T>(this.itemsExpected) : new ArrayList<T>());
		while (rs.hasNext()) {
			results.add(this.mapper.mapItem(rs.next()));
		}
		return results;
	}

}
