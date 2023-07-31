package webapp.controller;

import webapp.model.User;
import webserver.annotation.Authenticated;
import webserver.annotation.Controller;
import webserver.annotation.RequestMapping;
import webserver.http.message.HttpMethod;
import webserver.http.message.HttpResponse;
import webserver.view.View;

@Controller
public class UserListController {

	@Authenticated
	@RequestMapping(method = HttpMethod.GET, path = "/user/list")
	public HttpResponse userList(User user) {
		View listView = View.of("user/list", user);
		return HttpResponse.builder()
			.view(listView)
			.build();
	}

}
