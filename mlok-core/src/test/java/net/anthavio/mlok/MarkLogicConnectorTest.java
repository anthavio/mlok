package net.anthavio.mlok;

import java.net.URISyntaxException;

import net.anthavio.mlok.MarkLogicConnector;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.marklogic.xcc.ContentSource;

/**
 * 
 * @author martin.vanek
 *
 */
public class MarkLogicConnectorTest {

	@Test
	public void x() throws URISyntaxException {
		ContentSource contentSource = Mockito.mock(ContentSource.class);
		MarkLogicConnector connector = new MarkLogicConnector(contentSource);
		//connector.moduleInvoke(null);
	}
}
