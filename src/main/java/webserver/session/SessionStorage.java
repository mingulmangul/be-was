package webserver.session;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import webapp.db.Database;
import webapp.model.User;

public class SessionStorage {

	private static final Map<UUID, Session> storage = Maps.newConcurrentMap();

	private SessionStorage() {
	}

	public static Session createSession(String userId) {
		Session session = Session.from(userId);
		storage.put(session.getSessionId(), session);
		return session;
	}
	
	public static User findUserBySessionId(UUID sessionId) {
		Session session = storage.get(sessionId);
		if (session == null) {
			return null;
		}
		User user = Database.findUserById(session.getUserId());
		return user;
	}

}
