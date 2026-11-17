package ru.keich.mon.automation.script.version;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Comparator;

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
public class ScriptVersion implements Comparable  {
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
	
	@Override
	public int hashCode() {
		return Objects.hash(version);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScriptVersion sc) {
			return (this.id == sc.getId()) && this.name.equals(sc.getName());
		} else return false;
	}

	@Override
	public int compareTo(Object o) {
		Objects.requireNonNull(o);
		if (o instanceof ScriptVersion sc) 
			return this.getId().compareTo(sc.getId());
		else
			throw new ClassCastException();
	}

}