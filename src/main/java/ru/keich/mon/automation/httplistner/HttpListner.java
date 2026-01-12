package ru.keich.mon.automation.httplistner;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

@Entity
@Getter
@NoArgsConstructor
public class HttpListner {

	public static enum ContentType {
		HTML, JSON;
	}
	
	@Id
	private String path;

	private String scriptName;

	private boolean enable = false;
	
	private boolean permitAllAccess = false;
	
	private ContentType contentType;

	public boolean isValid() {
		return scriptName != null && !"".equals(scriptName) && path != null && !"".equals(path) && contentType != null;
	}

	public HttpListner setScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public HttpListner setPath(String path) {
		this.path = path;
		return this;
	}

	public HttpListner setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}
	
	public HttpListner setPermitAllAccess(boolean permitAllAccess) {
		this.permitAllAccess = permitAllAccess;
		return this;
	}

	public HttpListner setContentType(ContentType contentType) {
		this.contentType = contentType;
		return this;
	}

}
