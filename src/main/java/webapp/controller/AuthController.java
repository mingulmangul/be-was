package webapp.controller;

import webapp.db.Database;
import webapp.model.User;
import webserver.annotation.Controller;
import webserver.annotation.RequestBody;
import webserver.annotation.RequestMapping;
import webserver.http.message.HttpMethod;
import webserver.http.message.HttpResponse;
import webserver.http.message.HttpStatus;
import webserver.session.Cookie;
import webserver.session.Session;
import webserver.session.SessionStorage;
import webserver.view.View;

@Controller
public class AuthController {

	@RequestMapping(method = HttpMethod.GET, path = "/user/form")
	public HttpResponse formPage() {
		View formView = View.of("user/form");
		return HttpResponse.builder()
			.view(formView)
			.build();
	}

	@RequestMapping(method = HttpMethod.POST, path = "/user/create")
	public HttpResponse signUp(@RequestBody(name = "userId") String userId,
		@RequestBody(name = "password") String password,
		@RequestBody(name = "name") String name,
		@RequestBody(name = "email") String email) {
		User user = new User(userId, password, name, email);
		Database.addUser(user);
		return HttpResponse.builder()
			.status(HttpStatus.SEE_OTHER)
			.redirection("/user/login")
			.build();
	}

	@RequestMapping(method = HttpMethod.GET, path = "/user/login")
	public HttpResponse loginPage() {
		View loginView = View.of("user/login");
		return HttpResponse.builder()
			.view(loginView)
			.build();
	}

	@RequestMapping(method = HttpMethod.POST, path = "/user/login")
	public HttpResponse login(@RequestBody(name = "userId") String userId,
		@RequestBody(name = "password") String password) {
		User user = Database.findUserById(userId);
		if (user == null || !Database.verifyPassword(user, password)) {
			View loginFailedView = View.of("user/login_failed");
			return HttpResponse.builder()
				.status(HttpStatus.UNAUTHORIZED)
				.view(loginFailedView)
				.build();
		}
		// 세션 생성
		Session session = SessionStorage.createSession(userId);
		Cookie cookie = new Cookie(Session.SID, session.getSessionId().toString(), Session.MAX_AGE);
		// 쿠키 저장
		return HttpResponse.builder()
			.status(HttpStatus.SEE_OTHER)
			.setCookie(cookie)
			.redirection("/index")
			.build();
	}
}
