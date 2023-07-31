package webserver.http.message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import webserver.session.Cookie;
import webserver.view.View;

public class HttpResponse {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String LOCATION = "Location";

	private final HttpStatus status;
	private final HttpHeaderFields headerFields;
	private final byte[] body;

	private HttpResponse(HttpResponseBuilder builder) {
		this.status = builder.status;
		this.headerFields = builder.headerFields;
		this.body = builder.body;
	}

	public static HttpResponseBuilder builder() {
		return new HttpResponseBuilder();
	}

	public String getStatusMessage() {
		return status.getMessage();
	}

	public Set<Map.Entry<String, String>> getHeaderFields() {
		return headerFields.getAllFields();
	}

	public byte[] getBody() {
		return body;
	}

	public static class HttpResponseBuilder {

		private final HttpHeaderFields headerFields;
		private HttpStatus status;
		private byte[] body;

		private HttpResponseBuilder() {
			status = HttpStatus.OK;
			headerFields = new HttpHeaderFields();
			resetBody();
			// TODO : 요청의 general header는 응답에도 바로 저장하기
		}

		private void resetBody() {
			body = new byte[0];
			headerFields.setHeaderField(CONTENT_LENGTH, "0");
			if (headerFields.getValue(CONTENT_TYPE) != null) {
				headerFields.removeHeaderField(CONTENT_TYPE);
			}
		}

		public HttpResponseBuilder status(HttpStatus status) {
			if (this.status == HttpStatus.OK) {
				this.status = status;
			}
			return this;
		}

		public HttpResponseBuilder headerField(String key, String value) {
			this.headerFields.setHeaderField(key, value);
			return this;
		}

		public HttpResponseBuilder body(Path resourcePath) {
			try {
				this.body = Files.readAllBytes(resourcePath);
				setContentTypeAndLength(Files.probeContentType(resourcePath), body.length);
				return this;
			} catch (IOException e) {
				this.status = HttpStatus.SERVER_ERROR;
				return this;
			}
		}

		public HttpResponseBuilder view(View view) {
			if (this.headerFields.getValue(LOCATION) != null) {
				this.headerFields.removeHeaderField(LOCATION);
			}
			try {
				this.body = view.render();
				setContentTypeAndLength("text/html", body.length);
				return this;
			} catch (IOException e) {
				status = HttpStatus.SERVER_ERROR;
				return this;
			}
		}

		private void setContentTypeAndLength(String contentType, int contentLength) {
			if (contentType.startsWith("text")) {
				contentType = contentType.concat(";charset=UTF-8");
			}
			this.headerFields.setHeaderField(CONTENT_TYPE, contentType);
			this.headerFields.setHeaderField(CONTENT_LENGTH, String.valueOf(contentLength));
		}

		public HttpResponseBuilder redirection(String targetUrl) {
			if (this.body.length > 0) {
				resetBody();
			}
			return headerField(LOCATION, targetUrl);
		}

		public HttpResponseBuilder setCookie(Cookie cookie) {
			headerField("Set-Cookie", cookie.toCookieFieldValue());
			return this;
		}

		public HttpResponse build() {
			return new HttpResponse(this);
		}

	}

}
