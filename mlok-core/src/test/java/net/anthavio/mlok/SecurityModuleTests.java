package net.anthavio.mlok;

import net.anthavio.mlok.MarkLogicConnector;
import net.anthavio.mlok.security.SecurityModule;

import org.testng.annotations.Test;


/**
 * security module function require user with privileges...
 * 
 * @author martin.vanek
 *
 */
public class SecurityModuleTests {

	private MarkLogicConnector connector = new MarkLogicConnector("xcc://test:pass@192.168.88.103:8003/Security");

	@Test
	public void test() {
		SecurityModule module = new SecurityModule(connector);
		module.userExists("writer");
	}
}
