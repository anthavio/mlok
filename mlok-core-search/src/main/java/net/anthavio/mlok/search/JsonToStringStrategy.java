package net.anthavio.mlok.search;

import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * 
 * @author martin.vanek
 *
 */
public class JsonToStringStrategy implements ToStringStrategy {

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, boolean value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, byte value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, char value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, double value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, float value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, int value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, long value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, short value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, Object value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, boolean[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, byte[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, char[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, double[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, float[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, int[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, long[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, short[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder append(ObjectLocator locator, StringBuilder stringBuilder, Object[] value) {
		return stringBuilder.append(value);
	}

	@Override
	public StringBuilder appendStart(ObjectLocator parentLocator, Object parent, StringBuilder stringBuilder) {
		return stringBuilder.append('{');
	}

	@Override
	public StringBuilder appendEnd(ObjectLocator parentLocator, Object parent, StringBuilder stringBuilder) {
		return stringBuilder.append('}').append('\n');
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, boolean value) {
		return stringBuilder.append(fieldName);
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, byte value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, char value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, float value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, long value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, short value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, boolean[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, char[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, double[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, float[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, int[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, long[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, short[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder appendField(ObjectLocator parentLocator, Object parent, String fieldName,
			StringBuilder stringBuilder, Object[] value) {
		// TODO Auto-generated method stub
		return null;
	}

}
