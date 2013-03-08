package com.anthavio.mlok;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.PermissionDeniedDataAccessException;

import com.anthavio.mlok.query.XccAdhocFunction;
import com.anthavio.mlok.query.XccAdhocQuery;
import com.anthavio.mlok.result.XccMappers;
import com.anthavio.mlok.result.XccResultItemCallback;
import com.anthavio.mlok.result.XccResultItemMapper;
import com.anthavio.mlok.result.XccResultSequenceExtractor;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.DocumentFormat;
import com.marklogic.xcc.ModuleInvoke;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.RequestOptions;
import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.RequestPermissionException;
import com.marklogic.xcc.exceptions.UnimplementedFeatureException;
import com.marklogic.xcc.exceptions.XQueryException;
import com.marklogic.xcc.exceptions.XccConfigException;
import com.marklogic.xcc.exceptions.XccException;
import com.marklogic.xcc.types.XdmVariable;

/**
 * 
 * @author martin.vanek
 *
 */
public class MarkLogicConnector {

	public enum NullHandling {
		THROW_EXCEPTION, //be mean
		EMPTY_STRING, //convert to empty string
		IGNORE_SKIP; //be forgiving
	}

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ContentSource contentSource;

	private RequestOptions defaultOptions = new RequestOptions();

	private NullHandling nullParameterHandling = NullHandling.THROW_EXCEPTION;

	private char itemDelimeter = '|';

	public MarkLogicConnector(String uri) {
		this(URI.create(uri));
	}

	public MarkLogicConnector(URI uri) {
		try {
			this.contentSource = ContentSourceFactory.newContentSource(uri);
		} catch (XccConfigException xcx) {
			throw new UnhandledException(xcx);
		}
		this.defaultOptions.setCacheResult(false);
	}

	public MarkLogicConnector(String host, int port, String username, String password, String contentBase) {
		this.contentSource = ContentSourceFactory.newContentSource(host, port, username, password, contentBase);
		this.defaultOptions.setCacheResult(false);
	}

	public MarkLogicConnector(ContentSource contentSource) {
		this.contentSource = contentSource;
		this.defaultOptions.setCacheResult(false);
	}

	public <T> T execute(XccSessionCallback<T> action) {
		Session session = this.contentSource.newSession();
		try {
			return action.doInSession(session);
		} catch (XQueryException xqx) {
			String code = xqx.getCode();
			String formatString = xqx.getFormatString();
			if (code.equals("SEC-PRIV") || code.equals("SEC-PRIVDNE")) {
				throw new PermissionDeniedDataAccessException(formatString, xqx);
			} else if (code.equals("XDMP-UNDFUN") || code.equals("XDMP-TOOFEWARGS") || code.equals("XDMP-TOOMANYARGS")) {
				throw new InvalidDataAccessApiUsageException(formatString, xqx);
			} else if (formatString.indexOf("syntax error") != -1) {
				throw new InvalidDataAccessApiUsageException(formatString, xqx);
			}
			throw new DataRetrievalFailureException(xqx.getFormatString(), xqx);
		} catch (RequestPermissionException rpx) {
			throw new PermissionDeniedDataAccessException(rpx.getMessage(), rpx);
		} catch (RequestException rqx) {
			throw new DataRetrievalFailureException(rqx.getMessage(), rqx);
		} catch (XccException xcx) {
			throw new DataRetrievalFailureException(xcx.getMessage(), xcx);
		} catch (UnimplementedFeatureException ufx) {
			throw new InvalidDataAccessApiUsageException("Feature not implemented.", ufx);
		} catch (IllegalStateException isx) {
			throw new InvalidDataAccessResourceUsageException("Invalid session state.", isx);
		} finally {
			if (session != null && !session.isClosed()) {
				try {
					session.close();
				} catch (Exception x) {
					log.warn("Exception while closing Xcc Session", x);
				}
			}
		}
	}

	private <T> T execute(final Request request, final XccResultSequenceExtractor<T> extractor) {
		XccSessionCallback<T> callback = new XccSessionCallback<T>() {
			@Override
			public T doInSession(Session session) throws XccException {
				log.debug("Executing " + request);
				ResultSequence resultSequence = session.submitRequest(request);
				try {
					return extractor.extract(resultSequence);
				} finally {
					if (resultSequence != null && !resultSequence.isClosed()) {
						try {
							resultSequence.close();
						} catch (Exception x) {
							log.warn("Exception while closing Xcc ResultSequence", x);
						}
					}
				}
			}
		};
		return execute(callback);
	}

	//adhocQuery

	/**
	 * Suitable for streaming since does not have to return value
	 */
	public void adhocQuery(String xquery, XdmVariable... variables) {
		//XXX maybe use NOP_RESULT_EXTRACTOR
		execute(new AdhocQueryWithItemCallback(xquery, NOP_ITEM_CALLBACK, variables));
	}

	public void adhocQuery(String xquery, Object... arguments) {
		//XXX maybe use NOP_RESULT_EXTRACTOR
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		execute(new AdhocQueryWithItemCallback(xquery, NOP_ITEM_CALLBACK, variables));
	}

	public void adhocQuery(String xquery, Map<String, Object> parameters) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(parameters);
		//XXX maybe use NOP_RESULT_EXTRACTOR
		execute(new AdhocQueryWithItemCallback(xquery, NOP_ITEM_CALLBACK, variables));
	}

	public void adhocQuery(XccAdhocQuery query) {
		execute(new AdhocQueryWithItemCallback(query.getXquery(), NOP_ITEM_CALLBACK, query.getXdmVariables()));
	}

	//

	public void adhocQuery(String xquery, XccResultItemCallback callback, XdmVariable... variables) {
		execute(new AdhocQueryWithItemCallback(xquery, callback, variables));
	}

	public void adhocQuery(String xquery, XccResultItemCallback callback, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		execute(new AdhocQueryWithItemCallback(xquery, callback, variables));
	}

	public void adhocQuery(String xquery, XccResultItemCallback callback, Map<String, Object> parameters) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(parameters);
		execute(new AdhocQueryWithItemCallback(xquery, callback, variables));
	}

	public void adhocQuery(XccAdhocQuery query, XccResultItemCallback callback) {
		execute(new AdhocQueryWithItemCallback(query.getXquery(), callback, query.getXdmVariables()));
	}

	//

	public <T> List<T> adhocQuery(String xquery, XccResultItemMapper<T> mapper, XdmVariable... variables) {
		return execute(new AdhocQueryWithItemMapper<T>(xquery, mapper, variables));
	}

	public <T> List<T> adhocQuery(String xquery, XccResultItemMapper<T> mapper, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return execute(new AdhocQueryWithItemMapper<T>(xquery, mapper, variables));
	}

	public <T> List<T> adhocQuery(String xquery, XccResultItemMapper<T> mapper, Map<String, Object> parameters) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(parameters);
		return execute(new AdhocQueryWithItemMapper<T>(xquery, mapper, variables));
	}

	public <T> List<T> adhocQuery(XccAdhocQuery query, XccResultItemMapper<T> mapper) {
		return execute(new AdhocQueryWithItemMapper<T>(query.getXquery(), mapper, query.getXdmVariables()));
	}

	//

	public <T> List<T> adhocQuery(String xquery, Class<T> resultClass, XdmVariable... variables) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return execute(new AdhocQueryWithItemMapper<T>(xquery, mapper, variables));
	}

	public <T> List<T> adhocQuery(String xquery, Class<T> resultClass, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return execute(new AdhocQueryWithItemMapper<T>(xquery, mapper, variables));
	}

	public <T> List<T> adhocQuery(String xquery, Class<T> resultClass, Map<String, Object> parameters) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(parameters);
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return execute(new AdhocQueryWithItemMapper<T>(xquery, mapper, variables));
	}

	public <T> List<T> adhocQuery(XccAdhocQuery query, Class<T> resultClass) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		XdmVariable[] variables = query.getXdmVariables();
		return execute(new AdhocQueryWithItemMapper<T>(query.getXquery(), mapper, variables));
	}

	// adhocFunction

	public void adhocFunction(LibraryModule module, String functionName, XccResultItemCallback callback,
			XdmVariable... variables) {
		XccAdhocFunction function = new XccAdhocFunction(module, functionName, variables);
		execute(new AdhocQueryWithItemCallback(function, callback));
	}

	public void adhocFunction(String functionName, XccResultItemCallback callback, XdmVariable... variables) {
		adhocFunction(null, functionName, callback, variables);
	}

	public void adhocFunction(LibraryModule module, String functionName, XccResultItemCallback callback,
			Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		adhocFunction(module, functionName, callback, variables);
	}

	public void adhocFunction(String functionName, XccResultItemCallback callback, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		adhocFunction(null, functionName, callback, variables);
	}

	public void adhocFunction(LibraryModule module, String functionName, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		adhocFunction(module, functionName, NOP_ITEM_CALLBACK, variables);
	}

	public <T> T adhocFunctionOne(LibraryModule module, String functionName, XccResultItemMapper<T> mapper,
			XdmVariable... variables) {
		XccAdhocFunction function = new XccAdhocFunction(module, functionName, variables);
		List<T> results = execute(new AdhocQueryWithItemMapper<T>(function, mapper));
		if (results.isEmpty()) {
			throw new EmptyResultDataAccessException(1);
		} else if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		} else {
			return results.get(0);
		}
	}

	public <T> T adhocFunctionOne(String functionName, XccResultItemMapper<T> mapper, XdmVariable... variables) {
		return adhocFunctionOne(null, functionName, mapper, variables);
	}

	public <T> T adhocFunctionOne(LibraryModule module, String functionName, XccResultItemMapper<T> mapper,
			Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunctionOne(module, functionName, mapper, variables);
	}

	public <T> T adhocFunctionOne(String functionName, XccResultItemMapper<T> mapper, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunctionOne(null, functionName, mapper, variables);
	}

	public <T> T adhocFunctionOne(LibraryModule module, String functionName, Class<T> resultClass,
			XdmVariable... variables) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return adhocFunctionOne(module, functionName, mapper, variables);
	}

	public <T> T adhocFunctionOne(String functionName, Class<T> resultClass, XdmVariable... variables) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return adhocFunctionOne(null, functionName, mapper, variables);
	}

	public <T> T adhocFunctionOne(LibraryModule module, String functionName, Class<T> resultClass, Object... arguments) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunctionOne(module, functionName, mapper, variables);
	}

	public <T> T adhocFunctionOne(String functionName, Class<T> resultClass, Object... arguments) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunctionOne(null, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(LibraryModule module, String functionName, XccResultItemMapper<T> mapper,
			XdmVariable... variables) {
		XccAdhocFunction function = new XccAdhocFunction(module, functionName, variables);
		return execute(new AdhocQueryWithItemMapper<T>(function, mapper));
	}

	public <T> List<T> adhocFunction(String functionName, XccResultItemMapper<T> mapper, XdmVariable... variables) {
		return adhocFunction(null, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(LibraryModule module, String functionName, XccResultItemMapper<T> mapper,
			Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunction(module, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(String functionName, XccResultItemMapper<T> mapper, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunction(null, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(LibraryModule module, String functionName, Class<T> resultClass,
			XdmVariable... variables) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return adhocFunction(module, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(String functionName, Class<T> resultClass, XdmVariable... variables) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return adhocFunction(null, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(LibraryModule module, String functionName, Class<T> resultClass, Object... arguments) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunction(module, functionName, mapper, variables);
	}

	public <T> List<T> adhocFunction(String functionName, Class<T> resultClass, Object... arguments) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return adhocFunction(null, functionName, mapper, variables);
	}

	//moduleInvoke

	/**
	 * Suitable for streaming since does not have to return value
	 */
	public void moduleInvoke(String moduleUri, XccResultItemCallback callback, XdmVariable... variables) {
		execute(new ModuleInvokeWithItemCallback(moduleUri, callback, variables));
	}

	public void moduleInvoke(String moduleUri, XccResultItemCallback callback, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		execute(new ModuleInvokeWithItemCallback(moduleUri, callback, variables));
	}

	public <T> List<T> moduleInvoke(String moduleUri, XccResultItemMapper<T> mapper, XdmVariable... variables) {
		return execute(new ModuleInvokeWithItemMapper<T>(moduleUri, mapper, variables));
	}

	public <T> List<T> moduleInvoke(String moduleUri, XccResultItemMapper<T> mapper, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return execute(new ModuleInvokeWithItemMapper<T>(moduleUri, mapper, variables));
	}

	public <T> List<T> moduleInvoke(String moduleUri, Class<T> resultClass, XdmVariable... variables) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		return execute(new ModuleInvokeWithItemMapper<T>(moduleUri, mapper, variables));
	}

	public <T> List<T> moduleInvoke(String moduleUri, Class<T> resultClass, Object... arguments) {
		XccResultItemMapper<T> mapper = XccMappers.getMapperFor(resultClass);
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		return execute(new ModuleInvokeWithItemMapper<T>(moduleUri, mapper, variables));
	}

	//moduleUri

	public void moduleSpawn(String moduleUri, XdmVariable... variables) {
		execute(new ModuleSpawnWithoutCallback(moduleUri, variables));
	}

	public void moduleSpawn(String moduleUri, Object... arguments) {
		XdmVariable[] variables = XdmVariables.buildXdmVariables(arguments);
		execute(new ModuleSpawnWithoutCallback(moduleUri, variables));
	}

	/**
	 * Create content via ContentFactory.newContent(...)
	 * 
	 */
	public void insertContent(final Content... content) throws DataAccessException {
		if (content == null || content.length == 0) {
			throw new IllegalArgumentException("No content to insert");
		}
		for (int i = 0; i < content.length; ++i) {
			Content item = content[i];
			if (item == null) {
				throw new IllegalArgumentException("Content on index " + i + " is null");
			}
			if (StringUtils.isBlank(item.getUri())) {
				throw new IllegalArgumentException("Content on index " + i + " has blank uri");
			}
		}

		XccSessionCallback<Void> action = new XccSessionCallback<Void>() {
			@Override
			public Void doInSession(Session session) throws XccException {
				session.insertContent(content);
				return null;
			}
		};
		execute(action);
	}

	public void insertContent(String uri, String documentString) throws DataAccessException {
		ContentCreateOptions options = new ContentCreateOptions();
		Content content = ContentFactory.newContent(uri, documentString, options);
		insertContent(content);
	}

	public void insertContent(String uri, DocumentFormat format, InputStream stream) throws DataAccessException {
		ContentCreateOptions options = new ContentCreateOptions();
		options.setFormat(format);
		Content content = ContentFactory.newUnBufferedContent(uri, stream, options);
		insertContent(content);
	}

	public void insertContent(String uri, DocumentFormat format, Reader reader) throws DataAccessException {
		insertContent(uri, format, new ReaderInputStream(reader));
	}

	public void insertContent(String uri, DocumentFormat format, String src) {
		ContentCreateOptions options = new ContentCreateOptions();
		options.setFormat(format);
		Content content = ContentFactory.newContent(uri, src, options);
		insertContent(content);
	}

	/*
	public void insertDocument(String targetURI, DocumentFormat docFormat, Object src) throws DataAccessException {
		if (src == null) {
			throw new IllegalArgumentException("Document source cannot be null.");
		}
		try {
			if (DocumentFormat.BINARY.equals(docFormat)) {
				insertDocument(targetURI, docFormat, new ByteArrayInputStream(IOUtils.toByteArray(src)));
			} else if (DocumentFormat.TEXT.equals(docFormat)) {
				insertDocument(targetURI, docFormat, String.valueOf(src));
			} else if (DocumentFormat.XML.equals(docFormat)) {
				JAXBContext ctx = JAXBContext.newInstance(src.getClass());
				Marshaller marshaller = ctx.createMarshaller();
				ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
				marshaller.marshal(src, out);
				insertDocument(targetURI, docFormat, new ByteArrayInputStream(out.toByteArray()));
			}
		} catch (IOException ex) {
			throw new DataRetrievalFailureException("Failed to read source object.", ex);
		} catch (JAXBException ex) {
			throw new DataRetrievalFailureException("Failed to marshal source object.", ex);
		}
	}
	*/

	/*
		public <T> T function(LibraryModule module, String functionName, XccResultItemMapper<T> mapper,
				XdmVariable... variables) {
			StringBuilder sb = new StringBuilder();
			//imports
			if (module.getSourceUri() == null) {
				sb.append("declare namespace ");
				sb.append(module.getPrefix());
				sb.append(" = '");
				sb.append(module.getNamespaceUri());
				sb.append("';\n");
			} else {
				sb.append("import module namespace ");
				sb.append(module.getPrefix());
				sb.append(" = '");
				sb.append(module.getNamespaceUri());
				sb.append("' at '");
				sb.append(module.getSourceUri());
				sb.append("';\n");
			}
			//variables
			if (variables != null && variables.length != 0) {
				for (XdmVariable variable : variables) {
					sb.append("declare variable $");
					sb.append(variable.getName().toString());
					if (variable.getValue().getValueType() != null) {
						sb.append(" as ");
						sb.append(variable.getValue().getValueType().toString());
					}
					sb.append(" external;\n");
				}
			}
			//function call
			sb.append(module.getPrefix());
			sb.append(":");
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

			//Request request=session.;
			return executeOne(request, mapper, variables);
		}

		public ResultSequence queryInvoke(String query) {
			Session session = contentSource.newSession();
			AdhocQuery adhocQuery = session.newAdhocQuery(query);
			log.debug("Adhoc Query: " + query);
			return invoke(adhocQuery, session);
		}

		public ResultSequence queryInvoke(String query, Map<String, Object> parameters) {
			if (StringUtils.isBlank(query)) {
				throw new IllegalArgumentException("query is blank");
			}
			Session session = contentSource.newSession();
			AdhocQuery adhocQuery = session.newAdhocQuery(query);
			setExternalVariables(adhocQuery, parameters);
			return invoke(adhocQuery, session);
		}
		*/
	/*	
		public ResultSequence queryInvoke(String query, Object... parameters) {
			
		}
		public ResultSequence queryInvoke(String query, List<Object> parameters) {
			
		}
	*/

	public ResultSequence moduleInvoke(String moduleUri) {
		return moduleInvoke(moduleUri, null);
	}

	public ResultSequence moduleInvoke(String moduleUri, Map<String, Object> parameters) {
		if (StringUtils.isBlank(moduleUri)) {
			throw new IllegalArgumentException("Module URI is blank");
		}
		Session session = contentSource.newSession();
		ModuleInvoke moduleInvoke = session.newModuleInvoke(moduleUri);
		setExternalVariables(moduleInvoke, parameters);
		//
		return invoke(moduleInvoke, session);
	}

	private void setExternalVariables(Request request, Map<String, Object> parameters) {
		if (parameters != null && parameters.size() != 0) {
			Set<Entry<String, Object>> entrySet = parameters.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				String name = entry.getKey();
				Object value = entry.getValue();
				value = checkNull(name, value);
				if (value == null) {
					continue;
				} else if (value instanceof String) {
					log.debug("adding string varible '" + name + "' value '" + value + "'");
					request.setNewStringVariable(name, (String) value);
				} else if (value instanceof Number) {
					log.debug("adding integer varible '" + name + "' value '" + value + "'");
					request.setNewIntegerVariable(name, ((Number) value).intValue());
				} else if (value instanceof List<?>) {
					List<?> list = (List<?>) value;
					if (list.size() != 0) {
						String joined = join(name, list);
						log.debug("adding string varible '" + name + "' value '" + joined + "'");
						request.setNewStringVariable(name, joined);
					}
				} else if (value.getClass().isArray()) {
					if (Array.getLength(value) != 0) {
						String joined = join(name, value);
						log.debug("adding string varible '" + name + "' value '" + joined + "'");
						request.setNewStringVariable(name, joined);
					}
				} else {
					throw new IllegalArgumentException("Unsupported parameter class " + value.getClass().getName() + " value "
							+ value);
				}
			}
		}
	}

	private Object checkNull(String name, Object value) {
		if (value == null) {
			if (nullParameterHandling == NullHandling.THROW_EXCEPTION) {
				throw new IllegalArgumentException("Parameter " + name + " value is null");
			} else if (nullParameterHandling == NullHandling.EMPTY_STRING) {
				return "";
			} else if (nullParameterHandling == NullHandling.IGNORE_SKIP) {
				//well... ignore skip
				return null;
			} else {
				throw new IllegalStateException("Somebody forgot to handle " + nullParameterHandling);
			}
		} else {
			return value;
		}
	}

	private String join(String name, Collection<?> collection) {
		StringBuilder sb = new StringBuilder();
		for (Object item : collection) {
			item = checkNull(name, item);
			if (item != null) {
				if (item instanceof CharSequence || item instanceof Number) {
					sb.append(item);
					sb.append(itemDelimeter);
				} else {
					throw new IllegalArgumentException("Unsupported list item parameter class " + item.getClass().getName()
							+ " value " + item);
				}

			}
		}
		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length() - 1); // trailing itemDelimeter
		} else {
			//TODO still there can be more null or "" items so sb look like "||||"
			checkNull(name, null);//all was null
		}
		return sb.toString();
	}

	private String join(String name, Object array) {
		StringBuilder sb = new StringBuilder();
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++) {
			Object item = Array.get(array, i);
			item = checkNull(name, item);
			if (item != null) {
				if (item instanceof CharSequence || item instanceof Number) {
					sb.append(item);
					sb.append(itemDelimeter);
				} else {
					throw new IllegalArgumentException("Unsupported array item parameter class " + item.getClass().getName()
							+ " value " + item);
				}
			}
		}
		if (sb.length() != 0) {
			sb.deleteCharAt(sb.length() - 1); // trailing itemDelimeter
		} else {
			//TODO still there can be more null or "" items so sb look like "||||"
			checkNull(name, null);//all was null
		}
		return sb.toString();
	}

	private ResultSequence invoke(Request request, Session session) {
		request.setOptions(defaultOptions);
		try {
			ResultSequence result = session.submitRequest(request);
			return result;
		} catch (RequestException rx) {
			throw new UnhandledException(rx);
		} finally {
			if (defaultOptions.getCacheResult()) { //close only when returning string
				try {
					session.close();
				} catch (Exception x) {
					log.warn("Exception while closing session", x);
				}
			}
		}
	}

	public ContentSource getContentSource() {
		return contentSource;
	}

	public void setContentSource(ContentSource contentSource) {
		this.contentSource = contentSource;
	}

	public boolean getStreamResult() {
		return !defaultOptions.getCacheResult();
	}

	public void setStreamResult(boolean streaming) {
		this.defaultOptions.setCacheResult(!streaming);
	}

	public RequestOptions getDefaultOptions() {
		return defaultOptions;
	}

	public void setDefaultOptions(RequestOptions defaultOptions) {
		this.defaultOptions = defaultOptions;
	}

	private static final XccResultSequenceExtractor<Void> NOP_RESULT_EXTRACTOR = new XccResultSequenceExtractor<Void>() {

		@Override
		public Void extract(ResultSequence resultSequence) throws XccException, DataAccessException {
			return null;
		}
	};

	private static final XccResultItemCallback NOP_ITEM_CALLBACK = new XccResultItemCallback() {

		@Override
		public void processItem(ResultItem item) throws XccException {
			//do nothing
		}
	};

}
