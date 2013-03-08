package com.anthavio.mlok;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.types.XSAnyURI;
import com.marklogic.xcc.types.XSBase64Binary;
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
import com.marklogic.xcc.types.XSString;
import com.marklogic.xcc.types.XSTime;
import com.marklogic.xcc.types.XSYearMonthDuration;
import com.marklogic.xcc.types.XdmBinary;
import com.marklogic.xcc.types.XdmDuration;
import com.marklogic.xcc.types.XdmItem;

/**
 * 
 * @author martin.vanek
 *
 */
public class XccUtil {

	private static Logger log = LoggerFactory.getLogger(XccUtil.class);

	private XccUtil() {

	}

	/*
		public static String buildFunctionCallXQuery(LibraryModule module, String functionName, XdmVariable[] variables) {
			StringBuilder sb = new StringBuilder();

			sb.append("xquery version '1.0-ml';\n"); //XXX parametrize version

			//imports
			if (module != null) {
				if (module.getSourceUri() == null) {
					sb.append("\tdeclare namespace ");
					sb.append(module.getPrefix());
					sb.append(" = '");
					sb.append(module.getNamespaceUri());
					sb.append("';\n");
				} else {
					sb.append("\timport module namespace ");
					sb.append(module.getPrefix());
					sb.append(" = '");
					sb.append(module.getNamespaceUri());
					sb.append("' at '");
					sb.append(module.getSourceUri());
					sb.append("';\n");
				}
			}
			//variables
			if (variables != null && variables.length != 0) {
				for (XdmVariable variable : variables) {
					sb.append("\tdeclare variable $");
					sb.append(variable.getName().toString());
					if (variable.getValue().getValueType() != null) {
						sb.append(" as ");
						sb.append(variable.getValue().getValueType().toString());
					}
					sb.append(" external;");
					if (variable.getValue() instanceof XdmAtomic) {
						sb.append(" (: ").append(variable.getValue().asString()).append(" :)");
					}
					sb.append('\n');
				}
			}
			//function call
			sb.append("\t");
			if (module != null) {
				sb.append(module.getPrefix());
				sb.append(":");
			}
			sb.append(functionName);
			sb.append("(");
			if (variables != null && variables.length != 0) {
				for (int i = 0; i < variables.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append("$");
					sb.append(variables[i].getName());
				}
			}
			sb.append(")");
			String xquery = sb.toString();
			return xquery;
		}
	*/
	/**
	 * Returns the supplied {@link XdmItem} as a {@link BigDecimal} or returns
	 * null if the {@link XdmItem} does not represent a type that can be
	 * converted to a {@link BigDecimal}.
	 * <p/>
	 * @param resultItem The result item to be converted to a {@link BigDecimal}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link BigDecimal} or null.
	 */
	public static BigDecimal convert(ResultItem resultItem, BigDecimal defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSInteger) {
			return new BigDecimal(((XSInteger) item).asBigInteger());
		} else if (item instanceof XSDecimal) {
			return ((XSDecimal) item).asBigDecimal();
		} else if (item instanceof XSDouble) {
			return ((XSDouble) item).asBigDecimal();
		} else if (item instanceof XSFloat) {
			return ((XSFloat) item).asBigDecimal();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link BigInteger} or returns
	 * null if the {@link XdmItem} does not represent a type that can be
	 * converted to a {@link BigInteger}.
	 * <p/>
	 * @param resultItem The result item to be converted to a {@link BigInteger}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link BigInteger} or null.
	 */
	public static BigInteger convert(ResultItem resultItem, BigInteger defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSInteger) {
			return ((XSInteger) item).asBigInteger();
		} else if (item instanceof XSDecimal) {
			return ((XSDecimal) item).asBigDecimal().toBigInteger();
		} else if (item instanceof XSDouble) {
			return ((XSDouble) item).asBigDecimal().toBigInteger();
		} else if (item instanceof XSFloat) {
			return ((XSFloat) item).asBigDecimal().toBigInteger();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@code byte} array or returns
	 * null if the {@link XdmItem} does not represent a type that can be
	 * converted to a {@code byte} array.
	 * <p/>
	 * @param resultItem The result item to be converted to a {@code byte}
	 * array.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@code byte} array or null.
	 */
	public static byte[] convert(ResultItem resultItem, byte[] defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XdmBinary) {
			return ((XdmBinary) item).asBinaryData();
		} else if (item instanceof XSBase64Binary) {
			return ((XSBase64Binary) item).asBinaryData();
		} else if (item instanceof XSHexBinary) {
			return ((XSHexBinary) item).asBinaryData();
		} else {
			try {
				return IOUtils.toByteArray(item.asInputStream());
			} catch (UnsupportedOperationException ex) {
				log.error("Failed to convert result item to byte stream.", ex);
				return defaultValue;
			} catch (IOException ex) {
				log.error("Failed to stream result item.", ex);
				return defaultValue;
			}
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link Date} or returns null if
	 * the {@link XdmItem} does not represent a type that can be converted to a
	 * {@link Date}.
	 * <p/>
	 * @param resultItem The result item to be converted to {@link Date}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link Date} or null.
	 */
	public static Date convert(ResultItem resultItem, Date defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSDate) {
			return ((XSDate) item).asDate();
		} else if (item instanceof XSDateTime) {
			return ((XSDateTime) item).asDate();
		} else if (item instanceof XSTime) {
			return ((XSTime) item).asDate();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link Double} or returns null
	 * if the {@link XdmItem} does not represent a type that can be converted to
	 * a {@link Double}.
	 * <p/>
	 * @param resultItem The result item to be converted to a {@link Double}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link Double} or null.
	 */
	public static Double convert(ResultItem resultItem, Double defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSInteger) {
			return ((XSInteger) item).asBigInteger().doubleValue();
		} else if (item instanceof XSDecimal) {
			return ((XSDecimal) item).asBigDecimal().doubleValue();
		} else if (item instanceof XSDouble) {
			return ((XSDouble) item).asBigDecimal().doubleValue();
		} else if (item instanceof XSFloat) {
			return ((XSFloat) item).asBigDecimal().doubleValue();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link Float} or returns null
	 * if the {@link XdmItem} does not represent a type that can be converted to
	 * a {@link Float}.
	 * <p/>
	 * @param resultItem The result item to be converted to a {@link Float}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link Float} or null.
	 */
	public static Float convert(ResultItem resultItem, Float defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSInteger) {
			return ((XSInteger) item).asBigInteger().floatValue();
		} else if (item instanceof XSDecimal) {
			return ((XSDecimal) item).asBigDecimal().floatValue();
		} else if (item instanceof XSDouble) {
			return ((XSDouble) item).asBigDecimal().floatValue();
		} else if (item instanceof XSFloat) {
			return ((XSFloat) item).asBigDecimal().floatValue();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link GregorianCalendar} or
	 * returns null if the {@link XdmItem} does not represent a type that can be
	 * converted to {@link GregorianCalendar}.
	 * <p/>
	 * @param resultItem The result item to be converted to {@link GregorianCalendar}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link GregorianCalendar} or null.
	 */
	public static GregorianCalendar convert(ResultItem resultItem, GregorianCalendar defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSGDay) {
			return ((XSGDay) item).asGregorianCalendar();
		} else if (item instanceof XSGMonth) {
			return ((XSGMonth) item).asGregorianCalendar();
		} else if (item instanceof XSGMonthDay) {
			return ((XSGMonthDay) item).asGregorianCalendar();
		} else if (item instanceof XSGYear) {
			return ((XSGYear) item).asGregorianCalendar();
		} else if (item instanceof XSGYearMonth) {
			return ((XSGYearMonth) item).asGregorianCalendar();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as an {@link Integer} or returns
	 * null if the {@link XdmItem} does not represent a type that can be
	 * converted to an {@link Integer}.
	 * <p/>
	 * @param resultItem The result item to be converted to an {@link Integer}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return An {@link Integer} or null.
	 */
	public static Integer convert(ResultItem resultItem, Integer defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSInteger) {
			return ((XSInteger) item).asInteger();
		} else if (item instanceof XSDecimal) {
			return ((XSDecimal) item).asBigDecimal().intValue();
		} else if (item instanceof XSDouble) {
			return ((XSDouble) item).asBigDecimal().intValue();
		} else if (item instanceof XSFloat) {
			return ((XSFloat) item).asBigDecimal().intValue();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link Long} or returns null if
	 * the {@link XdmItem} does not represent a type that can be converted to a
	 * {@link Long}.
	 * <p/>
	 * @param resultItem The result item to be converted to {@link Long}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link Long} or null.
	 */
	public static Long convert(ResultItem resultItem, Long defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSInteger) {
			return ((XSInteger) item).asLong();
		} else if (item instanceof XSDecimal) {
			return ((XSDecimal) item).asBigDecimal().longValue();
		} else if (item instanceof XSDouble) {
			return ((XSDouble) item).asBigDecimal().longValue();
		} else if (item instanceof XSFloat) {
			return ((XSFloat) item).asBigDecimal().longValue();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the supplied {@link XdmItem} as a {@link URI} or returns null if
	 * the {@link XdmItem} does not represent a type that can be converted to a
	 * {@link URI}.
	 * <p/>
	 * @param resultItem The result item to be converted to {@link URI}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return A {@link URI} or null.
	 */
	public static URI convert(ResultItem resultItem, URI defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSAnyURI) {
			try {
				return ((XSAnyURI) item).asUri();
			} catch (URISyntaxException ex) {
				log.error("Invalid URI format: " + item.asString(), ex);
			}
		} else if (item instanceof XSString) {
			try {
				return new URI(item.asString());
			} catch (URISyntaxException ex) {
				log.error("Invalid URI format: " + item.asString(), ex);
			}
		}
		return defaultValue;
	}

	/**
	 * Returns the supplied {@link XdmItem} as an {@link XdmDuration} or returns
	 * null if the {@link XdmItem} does not represent a type that can be
	 * converted to an {@link XdmDuration}.
	 * <p/>
	 * @param resultItem The result item to be converted to an {@link XdmDuration}.
	 * @param defaultValue The default value if the result item cannot be cast.
	 * @return An {@link XdmDuration} or null.
	 */
	public static XdmDuration convert(ResultItem resultItem, XdmDuration defaultValue) {
		XdmItem item = resultItem.getItem();
		if (item instanceof XSDuration) {
			return ((XSDuration) item).asDuration();
		} else if (item instanceof XSDayTimeDuration) {
			return ((XSDayTimeDuration) item).asDuration();
		} else if (item instanceof XSYearMonthDuration) {
			return ((XSYearMonthDuration) item).asDuration();
		} else {
			return defaultValue;
		}
	}

}
