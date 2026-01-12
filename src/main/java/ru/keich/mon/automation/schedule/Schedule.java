package ru.keich.mon.automation.schedule;

import org.springframework.scheduling.support.CronExpression;

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
public class Schedule {

	@Id
	private String name;
	private String expression;
	private String scriptName;
	private boolean enable;

	public Schedule setName(String name) {
		this.name = name;
		return this;
	}

	public Schedule setExpression(String expression) {
		this.expression = expression;
		return this;
	}
	
	public Schedule setScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public Schedule setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public boolean isValid() {
		return name != null && !"".equals(name) && scriptName != null && !"".equals(scriptName) && CronExpression.isValidExpression(expression);
	}

	@Override
	public String toString() {
		return "Schedule [name=" + name + ", expression=" + expression + "]";
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		return name.equals(other.name);
	}

}
