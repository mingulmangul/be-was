package webserver.resolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import webapp.model.User;
import webserver.annotation.Authenticated;
import webserver.annotation.RequestBody;
import webserver.annotation.RequestParam;
import webserver.http.message.HttpRequest;
import webserver.http.message.HttpResponse;
import webserver.http.message.HttpStatus;
import webserver.mapping.ControllerMapping;
import webserver.mapping.UrlMapping;
import webserver.session.SessionStorage;
import webserver.view.View;

public class ControllerResolver {

	private final UrlMapping urlMapping = UrlMapping.getInstance();
	private final ControllerMapping controllerMapping = ControllerMapping.getInstance();

	public HttpResponse resolve(HttpRequest request) throws ReflectiveOperationException {
		Method method = urlMapping.getMethod(request.getHttpMethod(), request.getUrlPath());
		if (method == null) {
			return HttpResponse.builder()
				.status(HttpStatus.NOT_FOUND)
				.build();
		}
		return invoke(request, method);
	}

	private HttpResponse invoke(HttpRequest request, Method method) throws ReflectiveOperationException {
		// 세션 기반 인증
		if (method.isAnnotationPresent(Authenticated.class)) {
			UUID sessionId = request.getSessionId();
			if (sessionId == null || SessionStorage.findUserBySessionId(sessionId) == null) {
				View loginView = View.of("user/login");
				return HttpResponse.builder()
					.status(HttpStatus.UNAUTHORIZED)
					.view(loginView)
					.build();
			}
		}

		Object controller = controllerMapping.getControllerByMethod(method);
		Parameter[] parameters = method.getParameters();
		if (parameters.length == 0) {
			return (HttpResponse)method.invoke(controller);
		}

		List<Object> args = resolveArguments(request, parameters);
		// 파라미터 주입 실패 여부 확인
		for (int i = 0; i < parameters.length; i++) {
			// User는 null일 수 있음
			if (!parameters[i].getType().equals(User.class) && args.get(i) == null) {
				return HttpResponse.builder()
					.status(HttpStatus.BAD_REQUEST)
					.build();
			}
		}
		return (HttpResponse)method.invoke(controller, args.toArray());
	}

	private List<Object> resolveArguments(HttpRequest request, Parameter[] parameters) {
		return Arrays.stream(parameters)
			.map(parameter -> {
				if (request.isMethodGet() && parameter.isAnnotationPresent(RequestParam.class)) {
					RequestParam annotation = parameter.getAnnotation(RequestParam.class);
					String paramValue = request.getUrlParamValue(annotation.name());
					return URLDecoder.decode(paramValue, StandardCharsets.UTF_8);
				}
				if (request.isMethodPost() && parameter.isAnnotationPresent(RequestBody.class)) {
					RequestBody annotation = parameter.getAnnotation(RequestBody.class);
					String bodyValue = request.getBodyValue(annotation.name());
					return URLDecoder.decode(bodyValue, StandardCharsets.UTF_8);
				}
				UUID sessionId = request.getSessionId();
				if (sessionId != null
					&& parameter.getType().equals(User.class)) {
					return SessionStorage.findUserBySessionId(sessionId);
				}
				return null;
			}).collect(Collectors.toList());
	}

}
