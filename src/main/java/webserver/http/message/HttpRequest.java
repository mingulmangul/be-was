package webserver.http.message;

import java.util.Map;
import java.util.UUID;

import webserver.http.utils.HttpMessageParser;
import webserver.session.Session;

public class HttpRequest {

	private final HttpMethod httpMethod;
	private final URL url;
	private final HttpHeaderFields headerFields;
	private final HttpRequestBody body;

	public HttpRequest(HttpMethod httpMethod, URL url, HttpHeaderFields headerFields, HttpRequestBody body) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.headerFields = headerFields;
		this.body = body;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getUrlPath() {
		return url.getPath();
	}

	public String getUrlParamValue(String key) {
		return url.getParameterValue(key);
	}

	public String getBodyValue(String key) {
		return body.getValue(key);
	}

	public boolean forStaticResource() {
		return url.isStaticResource();
	}

	public boolean isMethodGet() {
		return httpMethod == HttpMethod.GET;
	}

	public boolean isMethodPost() {
		return httpMethod == HttpMethod.POST;
	}

	public UUID getSessionId() {
		String cookieFieldValue = headerFields.getValue("Cookie");
		if (cookieFieldValue == null) {
			return null;
		}
		Map<String, String> cookieMap = HttpMessageParser.parseCookies(cookieFieldValue);
		String sessionId = cookieMap.get(Session.SID);
		if (sessionId == null) {
			return null;
		}
		return UUID.fromString(sessionId);
	}

}
