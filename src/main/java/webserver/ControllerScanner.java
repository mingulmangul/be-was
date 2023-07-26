package webserver;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.annotation.Controller;
import webserver.annotation.RequestMapping;
import webserver.http.message.HttpResponse;
import webserver.mapping.UrlMapping;

public class ControllerScanner {

	private static final Logger logger = LoggerFactory.getLogger(ControllerScanner.class);

	private static final String PROJECT_PATH = "src/main/java/";
	private static final String CONTROLLER_PATH = "webapp/controller";
	private static final String EXTENSION = ".java";

	private final UrlMapping urlMapping = UrlMapping.getInstance();

	private ControllerScanner() {
	}

	public static void initialize() {
		try {
			ControllerScanner controllerScanner = new ControllerScanner();
			controllerScanner.scan();
		} catch (ReflectiveOperationException e) {
			logger.error(e.getMessage());
		}
	}

	private void scan() throws ReflectiveOperationException {
		File appDirectory = new File(PROJECT_PATH + CONTROLLER_PATH);
		if (!appDirectory.isDirectory()) {
			throw new ReflectiveOperationException();
		}
		scanControllers(appDirectory);
	}

	private void scanControllers(File directory) throws ReflectiveOperationException {
		File[] files = directory.listFiles();
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			String path = file.getPath();
			String className = path.substring(PROJECT_PATH.length(), path.lastIndexOf(EXTENSION))
				.replace("/", ".");
			logger.debug(className);
			Class<?> clazz = Class.forName(className);
			logger.debug(clazz.getName());
			if (clazz.isAnnotationPresent(Controller.class)) {
				scanRequestMappings(clazz);
			}
		}
	}

	private void scanRequestMappings(Class<?> controllerClass) throws ReflectiveOperationException {
		Object controller = controllerClass.getConstructor().newInstance();
		for (Method method : controllerClass.getMethods()) {
			RequestMapping annotation = method.getAnnotation(RequestMapping.class);
			if (annotation == null) {
				continue;
			}
			MethodHandle methodHandle = getMethodHandle(controller, method);
			urlMapping.add(annotation.method(), annotation.path(), methodHandle);
		}
	}

	private MethodHandle getMethodHandle(Object controller, Method method) throws ReflectiveOperationException {
		MethodType methodType = MethodType.methodType(HttpResponse.class);
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length > 0) {
			methodType = MethodType.methodType(HttpResponse.class, method.getParameterTypes());
		}
		logger.debug(method.getName());
		return MethodHandles.lookup()
			.findVirtual(controller.getClass(), method.getName(), methodType)
			.bindTo(controller);
	}

}