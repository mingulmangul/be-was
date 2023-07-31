package webapp.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import webapp.model.User;

public class Database {
	private static final Map<String, User> users = Maps.newConcurrentMap();

	public static void addUser(User user) {
		users.put(user.getUserId(), user);
	}

	public static User findUserById(String userId) {
		return users.get(userId);
	}

	public static boolean verifyPassword(User user, String password) {
		return user.getPassword().equals(password);
	}

	public static List<User> findAll() {
		return new ArrayList<>(users.values());
	}
}
