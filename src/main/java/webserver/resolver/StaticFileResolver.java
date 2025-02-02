package webserver.resolver;

import java.io.File;
import java.io.IOException;

import webserver.http.message.HttpResponse;
import webserver.http.message.HttpStatus;
import webserver.resolver.utils.FileMapper;

public class StaticFileResolver {

	public HttpResponse resolve(String url) throws IOException {
		File file = FileMapper.findFile(url);
		return HttpResponse.builder()
			.status(HttpStatus.OK)
			.body(file.toPath())
			.build();
	}

}
