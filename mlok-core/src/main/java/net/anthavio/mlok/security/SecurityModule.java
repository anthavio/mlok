package net.anthavio.mlok.security;

import net.anthavio.mlok.LibraryModule;
import net.anthavio.mlok.MarkLogicConnector;

import org.apache.commons.lang.StringUtils;


/**
 * Implementation of various security module functions
 * 
 * Functions must be executed against the security database and requires privileges
 * 
 * https://docs.marklogic.com/sec
 * 
 * @author martin.vanek
 *
 */
public class SecurityModule {

	private MarkLogicConnector connector;

	public SecurityModule(MarkLogicConnector connector) {
		if (connector == null) {
			throw new IllegalArgumentException("connector is null");
		}
		this.connector = connector;
	}

	public boolean roleExists(String rolename) {
		if (StringUtils.isBlank(rolename)) {
			throw new IllegalArgumentException("rolename is blank");
		}
		return connector.adhocFunctionOne(LibraryModule.SECURITY, "role-exists", Boolean.class, rolename);
	}

	public boolean userExists(String username) {
		if (StringUtils.isBlank(username)) {
			throw new IllegalArgumentException("username is blank");
		}
		return connector.adhocFunctionOne(LibraryModule.SECURITY, "user-exists", Boolean.class, username);
	}

	public void setDescription(String username, String description) {
		if (StringUtils.isBlank(username)) {
			throw new IllegalArgumentException("username is blank");
		}
		connector.adhocFunction(LibraryModule.SECURITY, "user-set-description", username, description);
	}

	public void setPassword(String username, String password) {
		if (StringUtils.isBlank(username)) {
			throw new IllegalArgumentException("username is blank");
		}
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("password is blank");
		}
		connector.adhocFunction(LibraryModule.SECURITY, "user-set-password", username, password);

	}

}
