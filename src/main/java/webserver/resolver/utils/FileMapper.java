package webserver.resolver.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FileMapper {

	private static final List<String> RESOURCE_PATH_LIST = new ArrayList<>();

	static {
		RESOURCE_PATH_LIST.add("src/main/resources/static");
		RESOURCE_PATH_LIST.add("src/main/resources/templates/");
	}

	private FileMapper() {
	}

	public static File findFile(String path) throws FileNotFoundException {
		File file;
		for (String resourcePath : RESOURCE_PATH_LIST) {
			file = new File(resourcePath + path);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}
		throw new FileNotFoundException();
	}
}
