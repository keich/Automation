package ru.keich.mon.automation.httplistner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Controller
@RequestMapping("/httplistner")
public class HttpListnerController {

	public static final String MAP_PATHPARAM = "pathParam";
	public static final String MAP_REQPARAM = "reqParam";
	public static final String MAP_OUTPUTHEADERS= "outputHeaders";
	public static final String MAP_HEADERS= "headers";
	public static final String MAP_HEADERS_REMOTE_ADDR= "remote_addr";
	public static final String MAP_HEADERS_REMOTE_PORT= "remote_port";
	
	private final HttpListnerService httpListnerService;

	public HttpListnerController(HttpListnerService httpListnerService) {
		this.httpListnerService = httpListnerService;
	}

	@GetMapping(value = { "/{path}/{*pathVar}", "/{path}" })
	@ResponseBody
	public Mono<ResponseEntity<String>> get(HttpServletRequest request, @RequestParam MultiValueMap<String, String> reqParam,
			@PathVariable(required = true) String path, @PathVariable(required = false) String pathVar) {
		var opt = httpListnerService.getHttListner(path);
		if(opt.isEmpty()) {
			return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(503)).body("Path not found"));
		}
		var httpListner = opt.get();
		if (!httpListner.isEnable()) {
			return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(503)).body("Path is disable"));
		}
		var contetntType = httpListner.getContentType() == HttpListner.ContentType.JSON ? MediaType.APPLICATION_JSON :MediaType.TEXT_HTML;
		
		var headers = new HashMap<String, List<String>>();
		var inHeaderNames = request.getHeaderNames();
		while(inHeaderNames.hasMoreElements()) {
			var name = inHeaderNames.nextElement();
			headers.put(name, Collections.list(request.getHeaders(name)));
		}
		headers.put(MAP_HEADERS_REMOTE_ADDR, List.of(request.getRemoteAddr()));
		headers.put(MAP_HEADERS_REMOTE_PORT, List.of(String.valueOf(request.getRemotePort())));
		
		var outputHeaders = new HashMap<String, List<String>>();
		var scriptParams = new HashMap<String, Object>();
		scriptParams.put(MAP_HEADERS, headers);
		scriptParams.put(MAP_REQPARAM, reqParam);
		scriptParams.put(MAP_PATHPARAM, pathVar);
		scriptParams.put(MAP_OUTPUTHEADERS, outputHeaders);
		return httpListnerService.run(httpListner, scriptParams)
				.map(data -> ResponseEntity.ok().headers(h -> outputHeaders.forEach(h::addAll)).contentType(contetntType).body(data))
				.onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(503)).body(e.getMessage())));
	}

}
