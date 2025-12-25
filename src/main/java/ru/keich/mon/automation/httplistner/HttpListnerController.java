package ru.keich.mon.automation.httplistner;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;


import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/httplistner")
public class HttpListnerController {

	private final HttpListnerService httpListnerService;

	public HttpListnerController(HttpListnerService httpListnerService) {
		this.httpListnerService = httpListnerService;
	}

	@GetMapping(value = { "/{path}/{pathVar}", "/{path}" })
	@ResponseBody
	public Mono<ResponseEntity<String>> get(@RequestParam MultiValueMap<String, String> reqParam,
			@PathVariable(required = true) String path, @PathVariable(required = false) String pathVar) {
		var opt = httpListnerService.getHttListner(path);
		if(opt.isEmpty()) {
			return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(503)).body("Path not found"));
		}
		var httpListner = opt.get();
		if (!httpListner.isEnable()) {
			return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(503)).body("Path is disable"));
		}
		return httpListnerService.run(httpListner, pathVar, reqParam).map(data -> {
			var contetntType = MediaType.TEXT_HTML;
			if(httpListner.getContentType() == HttpListner.ContentType.JSON) {
				contetntType = MediaType.APPLICATION_JSON;
			}
			return ResponseEntity.ok().contentType(contetntType).body(data);
		}).onErrorResume(e -> {
			return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(503)).body(e.getMessage()));
		});
	}

}
