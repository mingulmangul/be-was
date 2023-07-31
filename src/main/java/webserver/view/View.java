package webserver.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import webapp.db.Database;
import webapp.model.User;
import webserver.resolver.utils.FileMapper;

public class View {

	private static final String NOT_LOGGED_IN_TAG = "<li>.+로그인.+\\n.+회원가입.+<\\/li>";
	private static final String LOGGED_IN_TAG = "<li>.+(로그아웃|개인정보수정).*<\\/li>";
	private static final String PRE_WELCOME_TAG = "<li><a href=\"user/profile\" role=\"button\">";
	private static final String POST_WELCOME_TAG = " 님</a></li>";
	private static final String USER_ROW_TAG = "<tr>(\\n)+.*(scope=\"row\").+((\\n)+.*)*<\\/tr>";

	private final String viewName;
	private final User user;

	public View(String viewName, User user) {
		this.viewName = viewName;
		this.user = user;
	}

	public static View of(String viewName, User user) {
		return new View(viewName, user);
	}

	public static View of(String viewName) {
		return new View(viewName, null);
	}

	public byte[] render() throws IOException {
		File viewFile = FileMapper.findFile(viewName + ".html");
		String content = Files.readString(viewFile.toPath());

		if (user == null) {
			// 유저가 로그인하지 않은 상태
			content = content.replaceAll(LOGGED_IN_TAG, "");
		} else {
			// 유저가 로그인한 상태
			content = content.replaceAll(NOT_LOGGED_IN_TAG, PRE_WELCOME_TAG + user.getName() + POST_WELCOME_TAG);
		}

		// list.html
		if (viewName.equals("user/list")) {
			StringBuilder sb = new StringBuilder();
			List<User> users = Database.findAll();
			for (int i = 0; i < users.size(); i++) {
				sb.append("<tr>\n")
					.append("<th scope=\"row\">").append(i + 1).append("</th>")
					.append("<td>").append(users.get(i).getUserId()).append("</td>")
					.append("<td>").append(users.get(i).getName()).append("</td>")
					.append("<td>").append(users.get(i).getEmail()).append("</td>")
					.append("<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>\n")
					.append("</tr>\n");
			}
			content = content.replaceAll(USER_ROW_TAG, sb.toString());
		}

		// TODO: profile.html
		return content.getBytes();
	}
}
