package ru.keich.mon.automation.script.version;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.Instant;

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
@Data
@NoArgsConstructor
public class ScriptVersion {
	@Id
	private Instant id;
	@Column()
	private int version;
	@Column()
	private String note;
	@Column()
	private String name;
	@Column(columnDefinition = "TEXT")
	private String code;

	public ScriptVersion(int version, String note, String name, String code) {
		this.id = Instant.now();
		this.version = version;
		this.note = note;
		this.name = name;
		this.code = code;
	}

}
