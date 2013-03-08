package com.anthavio.mlok.result;

import static com.anthavio.mlok.XccUtil.convert;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.TypeMismatchDataAccessException;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.types.XSAnyURI;
import com.marklogic.xcc.types.XSBase64Binary;
import com.marklogic.xcc.types.XSBoolean;
import com.marklogic.xcc.types.XSDate;
import com.marklogic.xcc.types.XSDateTime;
import com.marklogic.xcc.types.XSDayTimeDuration;
import com.marklogic.xcc.types.XSDecimal;
import com.marklogic.xcc.types.XSDouble;
import com.marklogic.xcc.types.XSDuration;
import com.marklogic.xcc.types.XSFloat;
import com.marklogic.xcc.types.XSGDay;
import com.marklogic.xcc.types.XSGMonth;
import com.marklogic.xcc.types.XSGMonthDay;
import com.marklogic.xcc.types.XSGYear;
import com.marklogic.xcc.types.XSGYearMonth;
import com.marklogic.xcc.types.XSHexBinary;
import com.marklogic.xcc.types.XSInteger;
import com.marklogic.xcc.types.XSQName;
import com.marklogic.xcc.types.XSString;
import com.marklogic.xcc.types.XSTime;
import com.marklogic.xcc.types.XSUntypedAtomic;
import com.marklogic.xcc.types.XSYearMonthDuration;
import com.marklogic.xcc.types.XdmAtomic;
import com.marklogic.xcc.types.XdmAttribute;
import com.marklogic.xcc.types.XdmBinary;
import com.marklogic.xcc.types.XdmComment;
import com.marklogic.xcc.types.XdmDocument;
import com.marklogic.xcc.types.XdmDuration;
import com.marklogic.xcc.types.XdmElement;
import com.marklogic.xcc.types.XdmItem;
import com.marklogic.xcc.types.XdmNode;
import com.marklogic.xcc.types.XdmProcessingInstruction;
import com.marklogic.xcc.types.XdmText;

/**
 * 
 * @author martin.vanek
 *
 */
public class XccMappers {

	public static final String getCastErrorMessage(ResultItem resultItem, Class<?> resultClass) {
		StringBuilder sb = new StringBuilder("Cannot create ");
		sb.append(resultClass.getName()).append(" from ").append(resultItem.getItem().getClass().getName());
		sb.append(", index: ").append(resultItem.getIndex());
		sb.append(", itemType: ").append(resultItem.getItemType());
		sb.append(", valueType: ").append(resultItem.getValueType());
		sb.append(", asString: ").append(resultItem.asString());
		return sb.toString();
	}

	public static <T> XccResultItemMapper<T> getMapperFor(Class<T> resultType, JAXBContext jaxbContext) {
		return new JaxbResultItemMapper<T>(resultType, jaxbContext);
	}

	public static <T> XccResultItemMapper<T> getMapperFor(Class<T> resultType) {
		if (resultType.isAnnotationPresent(XmlType.class)) {
			return new JaxbResultItemMapper<T>(resultType);
		} else if (resultType.isAnnotationPresent(XmlRootElement.class)) {
			return new JaxbResultItemMapper<T>(resultType);
		} else {
			XccResultItemMapper<?> mapper = MAPPERS.get(resultType);
			if (mapper == null) {
				if (resultType.isAssignableFrom(XdmItem.class)) {
					mapper = MAPPERS.get(XdmItem.class); //last resort before fail....
				} else {
					throw new InvalidDataAccessApiUsageException("No XccResultItemMapper found for type " + resultType);
				}
			}
			return (XccResultItemMapper<T>) mapper;
		}
	}

	private static final XccResultItemMapper<XdmItem> XDM_ITEM_MAPPER = new XccResultItemMapper<XdmItem>() {
		@Override
		public XdmItem mapItem(ResultItem resultItem) {
			return resultItem.getItem();
		}

	};

	private static final XccResultItemMapper<Boolean> BOOLEAN_MAPPER = new XccResultItemMapper<Boolean>() {
		@Override
		public Boolean mapItem(ResultItem resultItem) {
			XdmItem item = resultItem.getItem();
			if (item instanceof XSBoolean) {
				return ((XSBoolean) resultItem.getItem()).asBoolean();
			} else {
				return Boolean.parseBoolean(item.asString());
			}
		}

	};

	private static final XccResultItemMapper<String> STRING_MAPPER = new XccResultItemMapper<String>() {
		@Override
		public String mapItem(ResultItem resultItem) {
			try {
				return resultItem.getItem().asString();
			} catch (UnsupportedOperationException ex) {
				String message = XccMappers.getCastErrorMessage(resultItem, String.class);
				throw new TypeMismatchDataAccessException(message);
			}
		}

	};

	private static final XccResultItemMapper<InputStream> STREAM_MAPPER = new XccResultItemMapper<InputStream>() {
		@Override
		public InputStream mapItem(ResultItem resultItem) throws DataAccessException {
			try {
				return resultItem.getItem().asInputStream();
			} catch (UnsupportedOperationException ex) {
				String message = XccMappers.getCastErrorMessage(resultItem, InputStream.class);
				throw new TypeMismatchDataAccessException(message, ex);
			}
		}

	};

	private static final XccResultItemMapper<Reader> READER_MAPPER = new XccResultItemMapper<Reader>() {
		@Override
		public Reader mapItem(ResultItem resultItem) throws DataAccessException {
			try {
				return resultItem.getItem().asReader();
			} catch (UnsupportedOperationException ex) {
				String message = XccMappers.getCastErrorMessage(resultItem, Reader.class);
				throw new TypeMismatchDataAccessException(message, ex);
			}
		}

	};

	private static final XccResultItemMapper<byte[]> BYTE_ARRAY_MAPPER = new XccResultItemMapper<byte[]>() {
		@Override
		public byte[] mapItem(ResultItem resultItem) throws DataAccessException {
			byte[] result = convert(resultItem, (byte[]) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, byte[].class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<BigInteger> BIG_INTEGER_MAPPER = new XccResultItemMapper<BigInteger>() {
		@Override
		public BigInteger mapItem(ResultItem resultItem) throws DataAccessException {
			BigInteger result = convert(resultItem, (BigInteger) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, BigInteger.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<Long> LONG_MAPPER = new XccResultItemMapper<Long>() {
		@Override
		public Long mapItem(ResultItem resultItem) throws DataAccessException {
			Long result = convert(resultItem, (Long) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, Long.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<Integer> INTEGER_MAPPER = new XccResultItemMapper<Integer>() {
		@Override
		public Integer mapItem(ResultItem resultItem) throws DataAccessException {
			Integer result = convert(resultItem, (Integer) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, Integer.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<BigDecimal> BIG_DECIMAL_MAPPER = new XccResultItemMapper<BigDecimal>() {
		@Override
		public BigDecimal mapItem(ResultItem resultItem) throws DataAccessException {
			BigDecimal result = convert(resultItem, (BigDecimal) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, BigDecimal.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<Double> DOUBLE_MAPPER = new XccResultItemMapper<Double>() {
		@Override
		public Double mapItem(ResultItem resultItem) throws DataAccessException {
			Double result = convert(resultItem, (Double) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, Double.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<Float> FLOAT_MAPPER = new XccResultItemMapper<Float>() {
		@Override
		public Float mapItem(ResultItem resultItem) throws DataAccessException {
			Float result = convert(resultItem, (Float) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, Float.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<Date> DATE_MAPPER = new XccResultItemMapper<Date>() {
		@Override
		public Date mapItem(ResultItem resultItem) throws DataAccessException {
			Date result = convert(resultItem, (Date) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, Date.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<XdmDuration> XDM_DURATION_MAPPER = new XccResultItemMapper<XdmDuration>() {
		@Override
		public XdmDuration mapItem(ResultItem resultItem) throws DataAccessException {
			XdmDuration result = convert(resultItem, (XdmDuration) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, XdmDuration.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final XccResultItemMapper<GregorianCalendar> CALENDAR_MAPPER = new XccResultItemMapper<GregorianCalendar>() {
		@Override
		public GregorianCalendar mapItem(ResultItem resultItem) throws DataAccessException {
			GregorianCalendar result = convert(resultItem, (GregorianCalendar) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, GregorianCalendar.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}
	};

	private static final XccResultItemMapper<URI> URI_MAPPER = new XccResultItemMapper<URI>() {
		@Override
		public URI mapItem(ResultItem resultItem) throws DataAccessException {
			URI result = convert(resultItem, (URI) null);
			if (result == null) {
				String message = XccMappers.getCastErrorMessage(resultItem, URI.class);
				throw new TypeMismatchDataAccessException(message);
			}
			return result;
		}

	};

	private static final Map<Class<?>, XccResultItemMapper<?>> MAPPERS;
	static {
		Map<Class<?>, XccResultItemMapper<?>> m = new HashMap<Class<?>, XccResultItemMapper<?>>();
		m.put(byte[].class, BYTE_ARRAY_MAPPER);
		m.put(BigDecimal.class, BIG_DECIMAL_MAPPER);
		m.put(BigInteger.class, BIG_INTEGER_MAPPER);
		m.put(Calendar.class, CALENDAR_MAPPER);
		m.put(Date.class, DATE_MAPPER);
		m.put(Double.class, DOUBLE_MAPPER);
		m.put(Float.class, FLOAT_MAPPER);
		m.put(GregorianCalendar.class, CALENDAR_MAPPER);
		m.put(InputStream.class, STREAM_MAPPER);
		m.put(Integer.class, INTEGER_MAPPER);
		m.put(Long.class, LONG_MAPPER);
		m.put(Number.class, BIG_DECIMAL_MAPPER);
		m.put(Object.class, XDM_ITEM_MAPPER);
		m.put(Reader.class, READER_MAPPER);
		m.put(Boolean.class, BOOLEAN_MAPPER);
		m.put(String.class, STRING_MAPPER);
		m.put(URI.class, URI_MAPPER);
		m.put(XdmAtomic.class, XDM_ITEM_MAPPER);
		m.put(XdmAttribute.class, XDM_ITEM_MAPPER); // Warning! Not implemented by current XCC library
		m.put(XdmBinary.class, XDM_ITEM_MAPPER);
		m.put(XdmComment.class, XDM_ITEM_MAPPER); // Warning! Not implemented by current XCC library
		m.put(XdmDocument.class, XDM_ITEM_MAPPER); // Warning! Not implemented by current XCC library
		m.put(XdmDuration.class, XDM_DURATION_MAPPER);
		m.put(XdmElement.class, XDM_ITEM_MAPPER);
		m.put(XdmItem.class, XDM_ITEM_MAPPER);
		m.put(XdmNode.class, XDM_ITEM_MAPPER);
		m.put(XdmProcessingInstruction.class, XDM_ITEM_MAPPER); // Warning! Not implemented by current XCC library
		m.put(XdmText.class, XDM_ITEM_MAPPER);
		m.put(XSAnyURI.class, XDM_ITEM_MAPPER);
		m.put(XSBase64Binary.class, XDM_ITEM_MAPPER);
		m.put(XSBoolean.class, XDM_ITEM_MAPPER);
		m.put(XSDate.class, XDM_ITEM_MAPPER);
		m.put(XSDateTime.class, XDM_ITEM_MAPPER);
		m.put(XSDayTimeDuration.class, XDM_ITEM_MAPPER);
		m.put(XSDecimal.class, XDM_ITEM_MAPPER);
		m.put(XSDouble.class, XDM_ITEM_MAPPER);
		m.put(XSDuration.class, XDM_ITEM_MAPPER);
		m.put(XSFloat.class, XDM_ITEM_MAPPER);
		m.put(XSGDay.class, XDM_ITEM_MAPPER);
		m.put(XSGMonth.class, XDM_ITEM_MAPPER);
		m.put(XSGMonthDay.class, XDM_ITEM_MAPPER);
		m.put(XSGYear.class, XDM_ITEM_MAPPER);
		m.put(XSGYearMonth.class, XDM_ITEM_MAPPER);
		m.put(XSHexBinary.class, XDM_ITEM_MAPPER);
		m.put(XSInteger.class, XDM_ITEM_MAPPER);
		m.put(XSQName.class, XDM_ITEM_MAPPER);
		m.put(XSString.class, XDM_ITEM_MAPPER);
		m.put(XSTime.class, XDM_ITEM_MAPPER);
		m.put(XSUntypedAtomic.class, XDM_ITEM_MAPPER);
		m.put(XSYearMonthDuration.class, XDM_ITEM_MAPPER);
		MAPPERS = Collections.synchronizedMap(m);
	}
}
