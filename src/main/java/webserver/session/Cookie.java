package webserver.session;

public class Cookie {

	private final String key;
	private final String value;
	private final Integer maxAge;

	public Cookie(String key, String value, Integer maxAge) {
		this.key = key;
		this.value = value;
		this.maxAge = maxAge;
	}

	public String toCookieFieldValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(key).append("=").append(value)
			.append("; Path=/");
		if (maxAge != null) {
			sb.append("; Max-Age=").append(maxAge);
		}
		return sb.toString();
	}
}
