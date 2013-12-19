package net.anthavio.mlok.search;

import net.anthavio.mlok.LibraryModule;
import net.anthavio.mlok.MarkLogicConnector;

import com.marklogic.appservices.search.OptionsType;
import com.marklogic.appservices.search.Report;
import com.marklogic.cts.Query;

/**
 * https://docs.marklogic.com/search
 * 
 * @author martin.vanek
 *
 */
public class SearchModule {

	private MarkLogicConnector connector;

	public SearchModule(MarkLogicConnector connector) {
		if (connector == null) {
			throw new IllegalArgumentException("connector is null");
		}
		this.connector = connector;
	}

	/**
	 * https://docs.marklogic.com/search:check-options
	 */
	public Report checkOptions(OptionsType options, Boolean strict) {
		return new Report();
	}

	/**
	 * https://docs.marklogic.com/search:estimate
	 */
	public long estimate(Query ctsQuery, OptionsType options) {
		return -1;
	}

	/**
	 * https://docs.marklogic.com/search:get-default-options
	 * @return
	 */
	public OptionsType getDefaultOptions() {
		//ObjectFactory of=new ObjectFactory();
		//of.createOptions();
		return connector.adhocFunctionOne(LibraryModule.SEARCH, "get-default-options", OptionsType.class);
	}

	/**
	 * https://docs.marklogic.com/search:parse
	 */
	public void parse(String qtext, OptionsType options, String output) {

	}

	public void removeConstraint() {

	}

	public void resolve() {

	}

	public void resolveNodes() {

	}

	public void search() {

	}

	public void suggest() {

	}

	public void unparse() {

	}

	public void values() {

	}
}
