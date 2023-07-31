package webserver.http.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.http.message.HttpHeaderFields;
import webserver.http.message.HttpMethod;
import webserver.http.message.HttpRequest;
import webserver.http.message.HttpRequestBody;
import webserver.http.message.URL;

public class HttpMessageParser {

	private static final Logger logger = LoggerFactory.getLogger(HttpMessageParser.class);

	private HttpMessageParser() {
	}

	public static HttpRequest parseHttpRequest(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String startLine = br.readLine();
		if (startLine == null) {
			return null;
		}
		logger.debug(startLine);
		String[] requestLineTokens = startLine.split(" ");
		if (requestLineTokens.length != 3) {
			return null;
		}
		HttpMethod httpMethod = HttpMethod.from(requestLineTokens[0]);
		URL url = parseURL(requestLineTokens[1]);
		HttpHeaderFields headerFields = parseHeaderFields(br);

		HttpRequestBody httpRequestBody = null;
		String contentLength = headerFields.getContentLength();
		if (contentLength != null) {
			httpRequestBody = parseBody(br, Integer.parseInt(contentLength));
		}
		return new HttpRequest(httpMethod, url, headerFields, httpRequestBody);
	}

	private static URL parseURL(String urlString) {
		Map<String, String> parameterMap = new HashMap<>();
		String[] tokens = urlString.split("\\?");
		if (tokens.length >= 2) {
			parameterMap = parseParameter(tokens[1], "&");
		}
		return new URL(tokens[0], parameterMap);
	}

	private static Map<String, String> parseParameter(String str, String delimiter) {
		Map<String, String> parameterMap = new HashMap<>();
		String[] paramList = str.split(delimiter);
		for (String param : paramList) {
			String[] tokens = param.split("=");
			String key = tokens[0];
			String value = "";
			if (tokens.length >= 2) {
				value = tokens[1];
			}
			parameterMap.put(key, value);
		}
		return parameterMap;
	}

	private static HttpHeaderFields parseHeaderFields(BufferedReader br) throws IOException {
		HttpHeaderFields headerFields = new HttpHeaderFields();
		String line = br.readLine();
		while (!"".equals(line)) {
			logger.debug(line);
			String[] tokens = line.split(": ?", 2);
			if (tokens.length == 2) {
				headerFields.setHeaderField(tokens[0], tokens[1]);
			}
			line = br.readLine();
		}
		return headerFields;
	}

	private static HttpRequestBody parseBody(BufferedReader br, Integer contentLength) throws IOException {
		char[] body = new char[contentLength];
		br.read(body, 0, contentLength);
		String bodyString = String.valueOf(body);
		logger.debug(bodyString);
		return new HttpRequestBody(parseParameter(bodyString, "&"));
	}

	public static Map<String, String> parseCookies(String cookieFieldValue) {
		return parseParameter(cookieFieldValue, "; ");
	}

}
