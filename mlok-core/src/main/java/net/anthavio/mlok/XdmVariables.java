package net.anthavio.mlok;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.marklogic.xcc.ValueFactory;
import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XName;
import com.marklogic.xcc.types.XdmValue;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author martin.vanek
 *
 */
public class XdmVariables {

	public static XdmVariable buildXdmvariable(String namespace, String name, ValueType valueType, Object value) {
		return ValueFactory.newVariable(new XName(namespace, name), ValueFactory.newValue(valueType, value));
	}

	public static XdmVariable buildXdmVariable(String name, ValueType valueType, Object value) {
		return buildXdmvariable(null, name, valueType, value);
	}

	public static XdmVariable[] buildXdmVariables(Map<String, Object> parameters) {
		if (parameters != null && parameters.size() != 0) {
			List<XdmVariable> variables = new ArrayList<XdmVariable>(parameters.size());
			Set<Entry<String, Object>> entrySet = parameters.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				String name = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof XdmVariable) {
					variables.add((XdmVariable) value);
				} else {
					checkForNull(value);
					XdmValue xvalue = convert(value);
					XName xname = new XName(name);
					variables.add(ValueFactory.newVariable(xname, xvalue));
				}
			}
			return variables.toArray(new XdmVariable[variables.size()]);
		} else {
			return null;
		}
	}

	public static XdmVariable[] buildXdmVariables(Object... values) {
		if (values != null && values.length != 0) {
			XdmVariable[] variables = new XdmVariable[values.length];
			for (int i = 0, j = 1; i < values.length; i++) {
				if (values[i] instanceof XdmVariable) {
					variables[i] = (XdmVariable) values[i];
				} else {
					Object value = values[i];
					checkForNull(value);
					XdmValue xvalue = convert(value);
					XName xname = new XName("param" + j);
					variables[i] = ValueFactory.newVariable(xname, xvalue);
					j++;
				}
			}
			return variables;
		} else {
			return null;
		}
	}

	private static void checkForNull(Object value) {
		if (value == null) {
			throw new IllegalArgumentException("Null values are not allowed.");
		}
	}

	private static XdmValue convert(Object value) {
		XdmValue xvalue;

		if (value == null) {
			throw new IllegalArgumentException("value is null");
		}
		if (value instanceof XdmValue) {
			xvalue = (XdmValue) value;
		} else if (value instanceof String) {
			xvalue = ValueFactory.newValue(ValueType.XS_STRING, value);
		} else if (value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
			//TODO Short, Byte
			xvalue = ValueFactory.newValue(ValueType.XS_INTEGER, value);
		} else if (value instanceof Float) {
			xvalue = ValueFactory.newValue(ValueType.XS_FLOAT, value);
		} else if (value instanceof Double) {
			xvalue = ValueFactory.newValue(ValueType.XS_DOUBLE, value);
		} else if (value instanceof BigDecimal) {
			xvalue = ValueFactory.newValue(ValueType.XS_DECIMAL, value);
		} else if (value instanceof Boolean) {
			xvalue = ValueFactory.newValue(ValueType.XS_BOOLEAN, value);
		} else if (value instanceof Date) {
			String date = sdfDateTime.format((Date) value);
			xvalue = ValueFactory.newValue(ValueType.XS_DATE_TIME, date);
		} else if (value instanceof Calendar) {
			String date = sdfDateTime.format(((Calendar) value).getTime());
			xvalue = ValueFactory.newValue(ValueType.XS_DATE_TIME, date);
		} else if (value instanceof URL || value instanceof URI) {
			xvalue = ValueFactory.newValue(ValueType.XS_ANY_URI, value.toString());
		} else if (value instanceof InputStream) {
			xvalue = ValueFactory.newValue(ValueType.XS_HEX_BINARY, value.toString());
		} else if (value instanceof byte[]) {
			xvalue = ValueFactory.newValue(ValueType.XS_HEX_BINARY, value);
		} else {
			throw new IllegalArgumentException("Unsupported " + value.getClass() + " of value " + value);
		}

		return xvalue;
	}

	//only formatting is thread safe, so no parsing please...
	private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") {//AbstractDateItem.DATETIME_FMT_STRING

		private static final long serialVersionUID = 1L;

		public Date parse(String source) throws java.text.ParseException {
			throw new UnsupportedOperationException("Don't call me!");
		};

		public Date parse(String text, java.text.ParsePosition pos) {
			throw new UnsupportedOperationException("Don't call me!");
		};
	};

}
