package com.anthavio.mlok.query;

import org.apache.commons.lang.StringUtils;

/**
 *
 * (/root[ns:element = 'something'])[100 to 200]
 * 
 * @author martin.vanek
 *
 */
public class XqyXpath extends XccAdhocQuery {

	private final String selector;

	private int firstIndex;

	private int lastIndex;

	public XqyXpath(String xpath) {
		this.selector = init(xpath);
	}

	public String getXquery() {

		if (firstIndex != 0 || lastIndex != 0) {
			if (firstIndex > lastIndex) {
				throw new IllegalStateException("Last " + lastIndex + " is less then from " + firstIndex);
			}
			StringBuilder sb = new StringBuilder();
			sb.append('(').append(selector).append(')');
			sb.append('[').append(firstIndex).append(" to ").append(lastIndex).append(']');
			setBody(sb.toString());
		} else {
			setBody(selector);
		}
		return super.getXquery();
	}

	public int getFirstIndex() {
		return firstIndex;
	}

	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}

	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	public void setRange(int firstIndex, int lastIndex) {
		this.firstIndex = firstIndex;
		this.lastIndex = lastIndex;
	}

	public String getSelector() {
		return selector;
	}

	public String toString() {
		return this.getXquery();
	}

	private String init(String xpath) {
		if (StringUtils.isBlank(xpath)) {
			throw new IllegalArgumentException("xpath is blank");
		}
		xpath = xpath.trim();
		int sidx = 0;
		int eidx = 0;
		final int xplen = xpath.length() - 1;
		if (xpath.charAt(0) == '(') {
			sidx = 1;
			int cbIdx = xpath.lastIndexOf(')');
			if (cbIdx == -1) {
				throw new IllegalArgumentException("Invalid xpath format " + xpath);
			} else if (cbIdx < xplen) {
				//parse range...
				int orng = xpath.indexOf('[', cbIdx + 1);
				int crng = xpath.indexOf(']', orng + 1);
				if (orng == -1 || crng == -1) {
					throw new IllegalArgumentException("Invalid range format " + xpath);
				}
				String range = xpath.substring(orng + 1, crng);
				String[] split = range.split(" to ");
				if (split.length != 2) {
					throw new IllegalArgumentException("Invalid range format " + xpath);
				}
				firstIndex = Integer.parseInt(split[0]);
				lastIndex = Integer.parseInt(split[1]);
			}
			eidx = cbIdx;
		}
		if (sidx != 0 || eidx != 0) {
			xpath = xpath.substring(sidx, eidx);
		}
		return xpath;
	}

}
