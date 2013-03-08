package com.anthavio.mlok.result;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.TypeMismatchDataAccessException;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.exceptions.XccException;

/**
 * 
 * @author martin.vanek
 *
 */
public class JaxbResultItemMapper<T> implements XccResultItemMapper<T> {

	private static Map<Class<?>, JAXBContext> cache = new HashMap<Class<?>, JAXBContext>();

	private final Class<T> resultType;

	private final JAXBContext jaxbContext;

	private final Unmarshaller unmarshaller;

	/**
	 * Creates single class JAXB context
	 */
	public JaxbResultItemMapper(Class<T> resultType) throws DataAccessException {
		if (resultType == null) {
			throw new IllegalArgumentException("resultType is null");
		}
		this.resultType = resultType;

		try {
			JAXBContext jaxbContext = cache.get(resultType);
			if (jaxbContext == null) {
				jaxbContext = JAXBContext.newInstance(resultType);
			}
			this.jaxbContext = jaxbContext;

			this.unmarshaller = this.jaxbContext.createUnmarshaller();
		} catch (JAXBException ex) {
			throw new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
	}

	/**
	 * If resultType class is part of bigger jaxbContext...
	 */
	public JaxbResultItemMapper(Class<T> resultType, JAXBContext jaxbContext) {
		if (resultType == null) {
			throw new IllegalArgumentException("resultType is null");
		}
		this.resultType = resultType;

		if (jaxbContext == null) {
			throw new IllegalArgumentException("jaxbContext is null");
		}
		this.jaxbContext = jaxbContext;

		try {
			this.unmarshaller = this.jaxbContext.createUnmarshaller();
		} catch (JAXBException ex) {
			throw new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
	}

	@Override
	public T mapItem(ResultItem resultItem) throws XccException {
		try {
			Object object = unmarshaller.unmarshal(resultItem.asInputStream());
			if (object instanceof JAXBElement<?>) {
				return ((JAXBElement<T>) object).getValue();
			}
			return (T) object;
			//return resultType.cast(object);
		} catch (JAXBException jaxbx) {
			throw new TypeMismatchDataAccessException(jaxbx.getMessage(), jaxbx);
		} catch (ClassCastException ccx) {
			String message = XccMappers.getCastErrorMessage(resultItem, resultType);
			throw new TypeMismatchDataAccessException(message, ccx);
		}
	}
}
