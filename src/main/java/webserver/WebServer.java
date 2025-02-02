package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
	private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
	private static final int DEFAULT_PORT = 8080;
	private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) throws Exception {
		int port = 0;
		if (args == null || args.length == 0) {
			port = DEFAULT_PORT;
		} else {
			port = Integer.parseInt(args[0]);
		}
		// 초기화
		ControllerScanner.initialize();

		// 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
		try (ServerSocket listenSocket = new ServerSocket(port)) {
			logger.info("Web Application Server started {} port.", port);

			ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

			// 클라이언트가 연결될때까지 대기한다.
			Socket connection;
			while ((connection = listenSocket.accept()) != null) {
				executor.execute(new RequestHandler(connection));
			}
		}
	}
}
