package ru.keich.mon.automation.httplistner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.vaadin.flow.data.provider.Query;

import reactor.core.publisher.Mono;
import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.scripting.ScriptCallBack;

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

@Service
public class HttpListnerService {

	private final HttpListnerRepository httpListnerRepository;
	private final ScheduleService scheduleService;
	public static final String MAP_PATHPARAM = "pathparam";
	public static final String MAP_REQPARAM = "reqparam";
	private final Map<String, HttpListner> cache = new ConcurrentHashMap<>();

	public HttpListnerService(HttpListnerRepository httpListnerRepository, ScheduleService scheduleService) {
		this.httpListnerRepository = httpListnerRepository;
		this.scheduleService = scheduleService;
	}

	public Stream<HttpListner> getAll(Query<HttpListner, Void> q) {
		return httpListnerRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}

	public int getCount(Query<HttpListner, Void> q) {
		return Math.toIntExact(httpListnerRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit()).count());
	}

	public Mono<String> run(HttpListner httpListner, String pathVar, MultiValueMap<String, String> reqParam) {
		Mono<String> result = Mono.create(sink -> {
			var callBack = new ScriptCallBack() {

				@Override
				public void onResult(String data) {
					sink.success(data);
				}

				@Override
				public void onError(Exception e) {
					sink.error(e);
				}

			};
			var scriptParams = new HashMap<String, Object>();
			scriptParams.put(MAP_REQPARAM, reqParam);
			scriptParams.put(MAP_PATHPARAM, pathVar);
			scheduleService.execute(httpListner.getScriptName(), scriptParams, callBack);
		});
		return result;
	}

	public void save(HttpListner httpListner) {
		httpListnerRepository.save(httpListner);
		cache.remove(httpListner.getPath());
	}

	public void delete(HttpListner httpListner) {
		httpListnerRepository.delete(httpListner);
		cache.remove(httpListner.getPath());
	}

	public Optional<HttpListner> getHttListner(String path) {
		return Optional.ofNullable(cache.compute(path, (key, hl) -> {
			if (hl == null) {
				var f = httpListnerRepository.findById(path);
				if(f.isPresent()) {
					return f.get();
				}
			}
			return hl;
		}));
	}

}
