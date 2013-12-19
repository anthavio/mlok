package net.anthavio.mlok.search;

import java.util.List;

import net.anthavio.mlok.MarkLogicConnector;

import org.testng.annotations.Test;

import com.marklogic.appservices.search.OptionsType;

/**
 * 
 * @author martin.vanek
 *
 */
public class SearchModuleTests {

	private MarkLogicConnector connector = new MarkLogicConnector("xcc://test:pass@192.168.88.103:8003/Documents");

	@Test
	public void test() {
		SearchModule module = new SearchModule(connector);
		OptionsType options = module.getDefaultOptions();
		List<Object> list = options.getAdditionalQueryOrAnnotationOrConcurrencyLevel();
		for (Object object : list) {
			System.out.println(object);
		}

	}
}
