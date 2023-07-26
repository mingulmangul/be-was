package webserver.http.message;

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

	public String getHttpVersion() {
		return "HTTP/1.1";
	}

	public String getUrlParamValue(String key) {
		return url.getParameterValue(key);
	}

	public boolean forStaticResource() {
		return url.isStaticResource();
	}

}
