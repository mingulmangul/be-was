package webapp.controller;

import webapp.model.User;
import webserver.annotation.Controller;
import webserver.annotation.RequestMapping;
import webserver.http.message.HttpMethod;
import webserver.http.message.HttpResponse;
import webserver.view.View;

@Controller
public class UserController {

	@RequestMapping(method = HttpMethod.GET, path = "/user/profile")
	public HttpResponse profile(User user) {
		View profileView = View.of("user/profile", user);
		return HttpResponse.builder()
			.view(profileView)
			.build();
	}

}
