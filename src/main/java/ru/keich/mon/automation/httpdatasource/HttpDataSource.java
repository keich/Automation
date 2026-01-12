package ru.keich.mon.automation.httpdatasource;

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
public class HttpDataSource {

	@Id
	private String name = "";
	
	private String baseUrl = "";
	
	public boolean isValid() {
		return !"".equals(name) && !"".equals(baseUrl) ;
	}
	
	public HttpDataSource setName(String name) {
		this.name = name;
		return this;
	}
	
	public HttpDataSource setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}
	
}
